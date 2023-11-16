package com.huangch.cloud.utils.office;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author huangch
 * @date 2023-09-24
 */
@SuppressWarnings("unused")
public class WordUtils {

    private final static String TABLE_PLACEHOLDER_PATTERN = "^\\{\\{.*\\..*}}$";

    /**
     * 删除word内的批注信息
     *
     * @param bytes word文件流
     * @return 删除批示后的文件流
     * @throws IOException 文件处理异常
     */
    public static byte[] deleteComment(byte[] bytes) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        XWPFDocument document = new XWPFDocument(bais);
        bais.close();

        // 遍历文档中的段落
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            // 遍历段落中的文本
            for (XWPFRun run : paragraph.getRuns()) {
                // 删除段落中的所有批注
                XmlCursor cursor = run.getCTR().newCursor();
                cursor.selectPath("./*");
                while (cursor.toNextSelection()) {
                    if ("commentReference".equals(cursor.getName().getLocalPart())) {
                        cursor.removeXml();
                    }
                }
                cursor.close();
            }
        }

        // 保存修改后的文档
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        document.write(baos);
        return baos.toByteArray();
    }

    /**
     * 复制段落样式
     *
     * @param sourceRun 源样式
     * @param targetRun 目标样式
     */
    private static void copyStyle(XWPFRun sourceRun, XWPFRun targetRun) {
        targetRun.setFontSize(sourceRun.getFontSizeAsDouble());
        targetRun.setFontFamily(sourceRun.getFontFamily());
        targetRun.setBold(sourceRun.isBold());
        targetRun.setColor(sourceRun.getColor());
        targetRun.setImprinted(sourceRun.isImprinted());
    }

    /**
     * 获取段落文字信息
     *
     * @param runs 段落列表
     * @return 段落文字
     */
    private static String getRunText(List<XWPFRun> runs) {
        if (CollectionUtil.isEmpty(runs)) {
            return null;
        }
        StringBuilder text = new StringBuilder();
        for (XWPFRun run : runs) {
            text.append(run.getText(0));
        }
        return text.toString();
    }

    /**
     * 渲染表格数据
     * 表格占位符“{{xxx.xxx}}”
     *
     * @param in   文件输入流
     * @param data 待填充数据
     * @return 文档对象
     * @throws IOException IO异常
     */
    public static XWPFDocument renderTable(InputStream in, Map<String, Object> data) throws IOException {
        XWPFDocument docx = new XWPFDocument(in);
        for (XWPFTable table : docx.getTables()) {
            // 判断是否需要填充，并且获取开始填充的行索引
            boolean renderFlag = false;
            int startDataRowIndex = 0;
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell tableCell : row.getTableCells()) {
                    String cellText = getRunText(tableCell.getParagraphArray(0).getRuns());
                    if (StrUtil.isNotBlank(cellText) && Pattern.matches(TABLE_PLACEHOLDER_PATTERN, cellText)) {
                        renderFlag = true;
                        break;
                    }
                }
                startDataRowIndex++;
            }
            if (!renderFlag) {
                continue;
            }

            XWPFTableRow baseRow = table.getRow(startDataRowIndex);
            for (int i = 0; i < baseRow.getTableCells().size(); i++) {
                XWPFTableCell baseCell = baseRow.getTableCells().get(i);
                // 匹配需要填充的列
                String cellText = getRunText(baseCell.getParagraphArray(0).getRuns());
                if (StrUtil.isNotBlank(cellText) && Pattern.matches(TABLE_PLACEHOLDER_PATTERN, cellText)) {
                    XWPFRun baseRun = baseCell.getParagraphArray(0).getRuns().get(0);
                    String dataPlaceHolder = cellText.replaceAll("[{}]", "");
                    // 提取对应数据
                    String[] dataPlaceHolderSplit = dataPlaceHolder.split("\\.");
                    Object tableDataList = data.get(dataPlaceHolderSplit[0]);
                    if (!(tableDataList instanceof List)) {
                        continue;
                    }
                    // 将数据填充至表格
                    String tableDataField = dataPlaceHolderSplit[1];
                    for (Object tableData : (List<?>) tableDataList) {
                        Map<String, Object> tableDataMap = BeanUtil.beanToMap(tableData);
                        Object tableCellData = tableDataMap.get(tableDataField);
                        if (tableCellData == null) {
                            continue;
                        }

                        XWPFRun newRun = table.createRow().getCell(i).getParagraphArray(0).insertNewRun(0);
                        copyStyle(baseRun, newRun);
                        newRun.setText(tableCellData.toString());
                    }
                }
            }
            table.removeRow(startDataRowIndex);
        }
        return docx;
    }

}

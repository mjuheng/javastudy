package com.huangch.cloud.utils.office;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlCursor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

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

}

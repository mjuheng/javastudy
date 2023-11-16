package com.huangch.cloud.utils.office;

import cn.hutool.core.collection.CollectionUtil;
import com.huangch.cloud.utils.office.enums.ExcelValidTypeEnum;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author huangch
 * @date 2023-10-17
 */
@Slf4j
@SuppressWarnings({"FieldCanBeLocal", "unused", "ExtractMethodRecommender", "UastIncorrectHttpHeaderInspection"})
public class ExcelTemplateUtils {

    private Workbook workbook;
    private final List<ExcelValidInfo> validList = new ArrayList<>();
    private final Class<?> workbookClz;
    private final int MAX_ROW_INDEX = 65535;

    public ExcelTemplateUtils(String filePath, Class<? extends Workbook> workbookClz) throws IOException {
        this.workbookClz = workbookClz;
        File file = new File(filePath);
        FileInputStream fis = new FileInputStream(file);

        assertWorkbookClz(workbookClz);
        if (workbookClz == XSSFWorkbook.class) {
            workbook = new XSSFWorkbook(fis);
        } else if (workbookClz == HSSFWorkbook.class) {
            workbook = new HSSFWorkbook(fis);
        }
    }

    private void assertWorkbookClz(Class<?> workbookClz) {
        if (workbookClz != XSSFWorkbook.class && workbookClz != HSSFWorkbook.class) {
            throw new IllegalArgumentException("invalid workbook type");
        }
    }

    /**
     * 添加下拉框限制
     *
     * @param sheetIndex         添加限制的sheet索引
     * @param validDataList      下拉框数据
     * @param validRowStartIndex 需要添加限制行的开始索引
     * @param attributeRowIndex  属性所在的行索引
     * @param attributeName      需要添加限制的属性名称
     */
    public void addSequenceValidationData(int sheetIndex, List<String> validDataList, int validRowStartIndex, int attributeRowIndex, String attributeName) {
        if (CollectionUtil.isEmpty(validDataList)) {
            throw new IllegalArgumentException("validDataList must specify at least one value");
        }
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        Row attributeRow = sheet.getRow(attributeRowIndex);
        int validColIndex = 0;
        for (Cell cell : attributeRow) {
            if (cell.getStringCellValue().equals(attributeName)) {
                addSequenceValidationData(sheetIndex, validDataList, validRowStartIndex, validColIndex);
            }
            validColIndex++;
        }
    }

    /**
     * 添加下拉框限制
     *
     * @param sheetIndex         添加限制的sheet索引
     * @param validDataList      下拉框数据
     * @param validRowStartIndex 需要添加限制行的开始索引
     * @param validColIndex      需要添加限制列的索引
     */
    public void addSequenceValidationData(int sheetIndex, List<String> validDataList, int validRowStartIndex, int validColIndex) {
        if (CollectionUtil.isEmpty(validDataList)) {
            throw new IllegalArgumentException("validDataList must specify at least one value");
        }
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        ExcelValidInfo excelValidInfo = new ExcelValidInfo();
        excelValidInfo.setSheetIndex(sheetIndex);
        excelValidInfo.setValidDataList(validDataList);
        excelValidInfo.setValidRowStartIndex(validRowStartIndex);
        excelValidInfo.setValidColIndex(validColIndex);
        excelValidInfo.setExcelValidType(ExcelValidTypeEnum.SEQUENCE);
        validList.add(excelValidInfo);
    }


    /**
     * 添加下拉框限制
     *
     * @param sheetIndex         添加限制的sheet索引
     * @param min                输入最小值
     * @param max                输入最大值
     * @param validRowStartIndex 需要添加限制行的开始索引
     * @param attributeRowIndex  属性所在的行索引
     * @param attributeName      需要添加限制的属性名称
     */
    public void addNumberValidationData(int sheetIndex, Integer min, Integer max, int validRowStartIndex, int attributeRowIndex, String attributeName) {

        Sheet sheet = workbook.getSheetAt(sheetIndex);
        Row attributeRow = sheet.getRow(attributeRowIndex);
        int validColIndex = 0;
        for (Cell cell : attributeRow) {
            if (cell.getStringCellValue().equals(attributeName)) {
                addNumberValidationData(sheetIndex, min, max, validRowStartIndex, validColIndex);
            }
            validColIndex++;
        }
    }

    /**
     * 添加数字限制
     *
     * @param sheetIndex         添加限制的sheet索引
     * @param min                输入最小值
     * @param max                输入最大值
     * @param validRowStartIndex 需要添加限制行的开始索引
     * @param validColIndex      需要添加限制列的索引
     */
    public void addNumberValidationData(int sheetIndex, Integer min, Integer max, int validRowStartIndex, int validColIndex) {
        String minFormula = min == null ? String.valueOf(Integer.MIN_VALUE) : min.toString();
        String maxFormula = max == null ? String.valueOf(Integer.MAX_VALUE) : max.toString();

        Sheet sheet = workbook.getSheetAt(sheetIndex);
        ExcelValidInfo excelValidInfo = new ExcelValidInfo();
        excelValidInfo.setSheetIndex(sheetIndex);
        excelValidInfo.setMin(minFormula);
        excelValidInfo.setMax(maxFormula);
        excelValidInfo.setValidRowStartIndex(validRowStartIndex);
        excelValidInfo.setValidColIndex(validColIndex);
        excelValidInfo.setExcelValidType(ExcelValidTypeEnum.NUMBER);
        validList.add(excelValidInfo);
    }

    /**
     * 开始导出模板
     *
     * @param os       输出流
     * @param fileName 输出的文件名
     */
    public void doExport(OutputStream os, String fileName) {
        doAddValidationData();
        exportToStream(os, fileName);
    }

    private void doAddValidationData() {
        for (ExcelValidInfo excelValidInfo : validList) {
            ExcelValidTypeEnum validType = excelValidInfo.getExcelValidType();
            Sheet sheet = workbook.getSheetAt(excelValidInfo.getSheetIndex());
            if (validType == ExcelValidTypeEnum.SEQUENCE) {
                // 下拉框限制
                String hideSheetName = randomSheetName();
                Sheet hideSheet = workbook.createSheet(hideSheetName);
                for (int i = 0; i < excelValidInfo.validDataList.size(); i++) {
                    hideSheet.createRow(i).createCell(0).setCellValue(excelValidInfo.validDataList.get(i));
                }
                Name category1Name = workbook.createName();
                category1Name.setNameName(hideSheetName);
                category1Name.setRefersToFormula(hideSheetName + "!" + "$A$1:$A$" + excelValidInfo.validDataList.size());
                DataValidationHelper helper = sheet.getDataValidationHelper();
                DataValidationConstraint constraint = helper.createFormulaListConstraint(hideSheetName);
                CellRangeAddressList addressList = new CellRangeAddressList(excelValidInfo.getValidRowStartIndex(), MAX_ROW_INDEX, excelValidInfo.validColIndex, excelValidInfo.validColIndex);
                DataValidation dataValidation = helper.createValidation(constraint, addressList);
                // 处理Excel兼容性问题
                if (dataValidation instanceof XSSFDataValidation) {
                    dataValidation.setSuppressDropDownArrow(true);
                    dataValidation.setShowErrorBox(true);
                } else {
                    dataValidation.setSuppressDropDownArrow(false);
                }
                sheet.addValidationData(dataValidation);
                workbook.setSheetHidden(workbook.getSheetIndex(hideSheetName), true);
            } else if (validType == ExcelValidTypeEnum.NUMBER) {
                // 数字限制
                DataValidationHelper helper = sheet.getDataValidationHelper();
                DataValidationConstraint constraint = helper.createNumericConstraint(
                        DataValidationConstraint.ValidationType.DECIMAL,
                        DataValidationConstraint.OperatorType.BETWEEN,
                        excelValidInfo.min,
                        excelValidInfo.max
                );

                CellRangeAddressList addressList = new CellRangeAddressList(excelValidInfo.getValidRowStartIndex(), MAX_ROW_INDEX, excelValidInfo.validColIndex, excelValidInfo.validColIndex);
                DataValidation validation = helper.createValidation(constraint, addressList);

                validation.setShowErrorBox(true);

                sheet.addValidationData(validation);
            }
        }
    }

    /**
     * 下载excel文件
     *
     * @param os       输出流
     * @param fileName 输出文件名
     */
    private void exportToStream(OutputStream os, String fileName) {
        try {
            if (os instanceof HttpServletResponse response) {
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.setHeader("Access-Control-Expose-Headers", "fileName");
                response.setHeader("fileName", URLEncoder.encode(fileName, StandardCharsets.UTF_8));
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
                response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                        + ";filename*=utf-8''" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
            }

            workbook.write(os);
            os.close();
            workbook.close();
        } catch (Exception e) {
            log.error("{}模板导出失败", fileName, e);
        }
    }

    /**
     * 生成随机sheet名
     *
     * @return sheet名
     */
    private String randomSheetName() {
        Random random = new Random();
        int sheetNameLength = 5;
        StringBuilder sheetName = new StringBuilder();

        for (int i = 0; i < sheetNameLength; i++) {
            sheetName.append(random.nextBoolean() ? (char) ('A' + random.nextInt(26)) : (char) ('a' + random.nextInt(26)));
        }
        return sheetName.toString();
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    @Data
    private class ExcelValidInfo {
        private int sheetIndex;
        private List<String> validDataList;
        private int validRowStartIndex;
        private int validColIndex;
        private String min;
        private String max;
        private ExcelValidTypeEnum excelValidType;
    }
}

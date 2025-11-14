package com.huangch.cloud.utils.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * @author huangch
 * @since 2025-09-11
 */
public class ExcelUtils {

    /**
     * 合并单元格
     *
     * @param sheet    sheet
     * @param firstRow 开始行索引
     * @param lastRow  结束行索引
     * @param firstCol 开始列索引
     * @param lastCol  结束列索引
     */
    public static void mergeCell(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        if (firstRow == lastRow && firstCol == lastCol) {
            return;
        }
        CellRangeAddress region = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
        sheet.addMergedRegion(region);
    }


    /**
     * 设置单元格边框
     *
     * @param sheet    sheet
     * @param firstRow 开始行索引
     * @param lastRow  结束行索引
     * @param firstCol 开始列索引
     * @param lastCol  结束列索引
     */
    public static void setCellBorder(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        // 添加表格数据
        for (int rowNum = firstRow; rowNum <= lastRow; rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                row = sheet.createRow(rowNum);
            }
            for (int colNum = firstCol; colNum <= lastCol; colNum++) {
                Cell cell = row.getCell(colNum);
                if (cell == null) {
                    cell = row.createCell(colNum);
                }
                CellStyle newStyle = sheet.getWorkbook().createCellStyle();
                newStyle.cloneStyleFrom(cell.getCellStyle());
                newStyle.setBorderTop(BorderStyle.THIN);
                newStyle.setBorderBottom(BorderStyle.THIN);
                newStyle.setBorderLeft(BorderStyle.THIN);
                newStyle.setBorderRight(BorderStyle.THIN);
                cell.setCellStyle(newStyle);
            }
        }
    }

    /**
     * 获取单元格的字符串值
     *
     * @param cell Excel单元格
     * @return 单元格内容（字符串），空返回 null
     */
    public static String getCellStringValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
        DataFormatter formatter = new DataFormatter();

        CellType cellType = cell.getCellType();
        if (cellType == CellType.FORMULA) {
            // 计算公式，返回计算结果类型
            cellType = evaluator.evaluateFormulaCell(cell);
        }
        switch (cell.getCellType()) {
            case FORMULA:
                // 计算公式后取值
                return formatter.formatCellValue(cell, evaluator);
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            default:
                return formatter.formatCellValue(cell);
        }
    }
}

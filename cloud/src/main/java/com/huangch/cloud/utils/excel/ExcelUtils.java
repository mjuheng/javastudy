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


    /**
     * 给整列批量写差值公式
     */
    public static void createRowFormulaForColumn(Sheet sheet, String formula, int startRow, int colIndex) {
        for (int i = startRow; i <= 10000; i++) {
            Row row = getOrCreateRow(sheet, i);
            Cell cell = getOrCreateCell(row, colIndex);

            String rowFormula = formula.replaceAll("\\b([A-Z]{1,3})(?=[<>=+\\-*/),])", "$1" + (i + 1));;
            cell.setCellFormula(rowFormula);
        }
    }

    /**
     * 获取或创建行
     *
     * @param sheet    工作表
     * @param rowIndex 行索引
     * @return 行对象
     */
    private static Row getOrCreateRow(Sheet sheet, int rowIndex) {
        Row row = sheet.getRow(rowIndex);
        return row != null ? row : sheet.createRow(rowIndex);
    }

    /**
     * 获取或创建单元格
     *
     * @param row      行对象
     * @param colIndex 列索引
     * @return 单元格对象
     */
    private static Cell getOrCreateCell(Row row, int colIndex) {
        Cell cell = row.getCell(colIndex);
        return cell != null ? cell : row.createCell(colIndex);
    }

    /**
     * 数字列索引转 Excel 列字母
     */
    public static String indexToExcelColumn(int colIndex) {
        StringBuilder sb = new StringBuilder();
        while (colIndex >= 0) {
            sb.insert(0, (char) ('A' + colIndex % 26));
            colIndex = colIndex / 26 - 1;
        }
        return sb.toString();
    }

    /**
     * Excel 列字母 → 数字索引（0-based）
     */
    public static int excelColumnToIndex(String column) {
        if (column == null || column.isEmpty()) {
            throw new IllegalArgumentException("column is empty");
        }

        int index = 0;
        char[] chars = column.toUpperCase().toCharArray();
        for (char c : chars) {
            if (c < 'A' || c > 'Z') {
                throw new IllegalArgumentException("invalid column: " + column);
            }
            index = index * 26 + (c - 'A' + 1);
        }
        return index - 1;
    }
}

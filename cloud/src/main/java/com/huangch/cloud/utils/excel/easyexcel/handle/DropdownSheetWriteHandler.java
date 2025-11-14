package com.huangch.cloud.utils.excel.easyexcel.handle;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 添加列的下拉框限制
 *
 * @author huangch
 * @since 2025-08-25
 */
public class DropdownSheetWriteHandler implements SheetWriteHandler {

    private  int firstRow = 1;
    private  int lastRow = 65535;
    private final int colIndex;
    private final String[] options;

    public DropdownSheetWriteHandler(List<String> options, int firstRow, int lastRow, int colIndex) {
        this.options = options.toArray(new String[0]);
        this.firstRow = firstRow;
        this.lastRow = lastRow;
        this.colIndex = colIndex;
    }

    public DropdownSheetWriteHandler(List<String> options, int colIndex) {
        this.options = options.toArray(new String[0]);
        this.colIndex = colIndex;
    }

    public DropdownSheetWriteHandler(List<String> options, Field field) {
        ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
        if (excelProperty == null) {
            throw new RuntimeException(String.format("字段：%s，需要被ExcelProperty注解", field.getName()));
        }

        Class<?> clz = field.getDeclaringClass();
        int colIndex = -1;
        for (Field declaredField : clz.getDeclaredFields()) {
            if (!declaredField.isAnnotationPresent(ExcelProperty.class)) {
                continue;
            }
            colIndex++;
            if (declaredField.equals(field)) {
                break;
            }
        }

        this.colIndex = colIndex;
        this.options = options.toArray(new String[0]);
    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        Sheet sheet = writeSheetHolder.getSheet();

        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = null;
        if (String.join(",", options).length() > 255) {
            // 创建隐藏 sheet 存储选项
            Sheet hiddenSheet = sheet.getWorkbook().createSheet("hidden_dept");
            for (int i = 0; i < options.length; i++) {
                hiddenSheet.createRow(i).createCell(0).setCellValue(options[i]);
            }
            // 创建名称管理
            Name namedCell = sheet.getWorkbook().createName();
            namedCell.setNameName(hiddenSheet.getSheetName());
            namedCell.setRefersToFormula("hidden_dept!$A$1:$A$" + options.length);

            constraint = helper.createFormulaListConstraint(namedCell.getNameName());
        } else {
            constraint = helper.createExplicitListConstraint(options);
        }
        CellRangeAddressList rangeList = new CellRangeAddressList(firstRow, lastRow, colIndex, colIndex);
        DataValidation validation = helper.createValidation(constraint, rangeList);
        // 兼容性设置
        if (validation instanceof XSSFDataValidation) {
            validation.setSuppressDropDownArrow(true);
            validation.setShowErrorBox(true);
        }
        sheet.addValidationData(validation);
    }
}

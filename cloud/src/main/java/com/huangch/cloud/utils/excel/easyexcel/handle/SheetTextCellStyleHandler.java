package com.huangch.cloud.utils.excel.easyexcel.handle;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.handler.context.SheetWriteHandlerContext;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * @author huangch
 * @since 2025-11-13
 */
@SuppressWarnings("FieldCanBeLocal")
public class SheetTextCellStyleHandler implements SheetWriteHandler {

    private final int firstRow = 1;
    private final int lastRow = 65535;

    @Override
    public void afterSheetCreate(SheetWriteHandlerContext context) {
        Sheet sheet = context.getWriteSheetHolder().getSheet();
        Workbook workbook = sheet.getWorkbook();

        DataFormat dataFormat = workbook.createDataFormat();
        short textFormat = dataFormat.getFormat("@"); // “@” 表示文本格式

        CellStyle textStyle = workbook.createCellStyle();
        textStyle.setDataFormat(textFormat);

        // 批量设置列样式
        for (int i = firstRow; i <= lastRow; i++) {
            sheet.setDefaultColumnStyle(i, textStyle);
        }
    }
}

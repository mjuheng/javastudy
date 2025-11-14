package com.huangch.cloud.utils.excel;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.style.column.AbstractColumnWidthStyleStrategy;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.Lists;
import org.apache.poi.ss.usermodel.Cell;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author huangch
 * @since 2025-09-11
 */
public class EasyExcelUtils {


    /**
     * 自定义数据导出
     *
     * @param dataList              全部数据列表
     * @param exportField           key.sheet页对应数据key key.字段名 value.字段
     * @param sheetKeyMap           key.sheet页对应数据key value.sheet页名称
     * @param getDataSheetKeyColumn 获取sheet页对应数据的字段
     */
    public static void customExport(List<?> dataList, Map<String, Map<String, String>> exportField, Map<String, String> sheetKeyMap, Function<Object, Object> getDataSheetKeyColumn, OutputStream os) {
        ExcelWriter excelWriter = EasyExcel.write(os).registerWriteHandler(new AbstractColumnWidthStyleStrategy() {
            @Override
            protected void setColumnWidth(WriteSheetHolder writeSheetHolder, List<WriteCellData<?>> cellDataList, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
                writeSheetHolder.getSheet().setColumnWidth(cell.getColumnIndex(), 16 * 256);
            }
        }).build();

        AtomicInteger sheetNo = new AtomicInteger(0);
        sheetKeyMap.forEach((sheetKey, sheetName) -> {
            List<List<Object>> customExportData = customExportData(dataList, exportField, sheetKey, getDataSheetKeyColumn);
            excelWriter.write(customExportData,
                    EasyExcel.writerSheet(sheetNo.getAndIncrement(), sheetName).head(customExportHead(exportField, sheetKey)).build());
        });

        excelWriter.finish();
        excelWriter.close();
    }


    /**
     * 获取自定义导出表头信息
     *
     * @param exportField key.sheet页对应数据key key.字段名 value.字段
     * @param sheetKey    sheet页对应的key
     * @return 自定义表头
     */
    private static List<List<String>> customExportHead(Map<String, Map<String, String>> exportField, String sheetKey) {
        List<List<String>> res = new ArrayList<>();
        exportField.get(sheetKey).forEach((headName, fieldName) -> {
            res.add(Lists.newArrayList(headName));
        });
        return res;
    }

    /**
     * 获取自定义导出数据
     *
     * @param exportField           key.sheet页对应数据key key.字段名 value.字段
     * @param dataList              数据列表
     * @param sheetKey              sheet页对应的数据key
     * @param getDataSheetKeyColumn 获取sheet页对应数据的字段
     * @return 自定义数据列表
     */
    private static List<List<Object>> customExportData(List<?> dataList, Map<String, Map<String, String>> exportField, String sheetKey, Function<Object, Object> getDataSheetKeyColumn) {
        Map<String, String> fieldMap = exportField.get(sheetKey);

        dataList = dataList.stream().filter(e -> sheetKey.equals(getDataSheetKeyColumn.apply(e))).collect(Collectors.toList());
        List<List<Object>> res = new ArrayList<>();
        for (Object vo : dataList) {
            List<Object> data = new ArrayList<>();
            fieldMap.forEach((headName, fieldName) -> {
                Object fieldValue = ReflectUtil.getFieldValue(vo, fieldName);
                Field field = ReflectUtil.getField(vo.getClass(), fieldName);
                // 转换时间
                if (fieldValue instanceof Date && field.isAnnotationPresent(JsonFormat.class)) {
                    fieldValue = DateUtil.format((Date) fieldValue, field.getAnnotation(JsonFormat.class).pattern());
                }
                data.add(fieldValue);
            });
            res.add(data);
        }
        return res;
    }
}

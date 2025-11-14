package com.huangch.cloud.utils.excel.easyexcel.listener;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.huangch.cloud.utils.excel.easyexcel.annotation.ExcelValid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 通用 EasyExcel 校验 Listener（即时抛异常）
 * <p>
 * 用于在解析 Excel 数据时进行字段校验，包括必填、字典值、日期格式和数字格式等校验。
 * 所有校验失败将立即抛出异常中断解析过程。
 * </p>
 */
@NoArgsConstructor
@Slf4j
public class EasyExcelListener<T> extends AnalysisEventListener<T> {

    /**
     * 存储解析成功的数据列表
     */
    @Getter
    private final List<T> dataList = new ArrayList<>();
    private boolean autoConvertFieldDict = false;

    /**
     * 字段字典映射，key为字段名，value.key为该字段允许的值集合 value.value为该字段的字段映射值
     */
    private Map<String, Map<String, String>> fieldDictMap = new HashMap<>();

    /**
     * 构造函数
     *
     * @param fieldDictMap         字段字典映射
     * @param autoConvertFiledDict 是否自动转换字段字典
     */
    public EasyExcelListener(Map<String, Map<String, String>> fieldDictMap, boolean autoConvertFiledDict) {
        this.fieldDictMap = fieldDictMap;
        this.autoConvertFieldDict = autoConvertFiledDict;
    }

    /**
     * 每解析一行数据时调用此方法，进行字段校验
     *
     * @param data    当前行解析出的数据对象
     * @param context 解析上下文信息
     */
    @Override
    public void invoke(T data, AnalysisContext context) {
        int rowIndex = context.readRowHolder().getRowIndex() + 1;
        // 遍历对象的所有字段进行校验
        for (Field field : data.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            try {
                ExcelValid valid = field.getAnnotation(ExcelValid.class);

                Object value = field.get(data);

                // 获取列名
                String colName = resolveColName(field);

                // 必填校验
                if (valid != null && valid.required() && ObjectUtils.isEmpty(value)) {
                    throw new RuntimeException("第" + rowIndex + "行，" + colName + "不能为空");
                }

                String strVal = value == null ? "" : value.toString().trim();
                if (!strVal.isEmpty() && value instanceof String) {
                    // 字典校验
                    Map<String, String> dictMap = fieldDictMap.getOrDefault(field.getName(), new HashMap<>());
                    if (!dictMap.isEmpty()) {
                        if (!dictMap.containsKey(strVal)) {
                            throw new RuntimeException("第" + rowIndex + "行，" + colName + "【" + strVal + "】不存在");
                        }
                        if (autoConvertFieldDict) {
                            field.set(data, dictMap.get(strVal));
                        }
                    }

                    // 日期校验（String 字段接收）
                    if (valid != null && StringUtils.isNotBlank(valid.datePattern()) && !isValidDate(strVal, valid.datePattern())) {
                        throw new RuntimeException("第" + rowIndex + "行，" + colName + "【" + strVal + "】不是合法日期，正确格式：" + valid.datePattern());
                    }

                    // 数字校验（String 字段接收）
                    if (valid != null && valid.number() && !isNumeric(strVal)) {
                        throw new RuntimeException("第" + rowIndex + "行，" + colName + "【" + strVal + "】不是合法数字");
                    }
                }

            } catch (IllegalAccessException e) {
                throw new RuntimeException("第" + rowIndex + "行，字段读取失败：" + e.getMessage(), e);
            }
        }
        dataList.add(data);
    }

    /**
     * 处理解析过程中发生的异常
     *
     * @param exception 解析异常
     * @param context   解析上下文信息
     * @throws Exception 抛出处理后的异常
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        int rowIndex = context.readRowHolder().getRowIndex() + 1;

        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException ex = (ExcelDataConvertException) exception;
            rowIndex = ex.getRowIndex() + 1;
            Field field = ex.getExcelContentProperty().getField();
            String colName = resolveColName(field);

            if (Date.class.isAssignableFrom(field.getType())) {
                String pattern = "yyyy-MM-dd";
                DateTimeFormat dateTimeFormat = field.getAnnotation(DateTimeFormat.class);
                if (dateTimeFormat != null && !"".equals(dateTimeFormat.value())) {
                    pattern = dateTimeFormat.value();
                }
                throw new RuntimeException("第" + rowIndex + "行，" + colName + "【" + ex.getCellData().getStringValue() + "】不是正确的日期格式（" + pattern + "）");
            } else if (BigDecimal.class.isAssignableFrom(field.getType()) || Number.class.isAssignableFrom(field.getType())) {
                throw new RuntimeException("第" + rowIndex + "行，" + colName + "【" + ex.getCellData().getStringValue() + "】不是正确的数字格式");
            }
        }
        throw exception;
    }

    /**
     * 所有数据解析完成后调用此方法
     *
     * @param context 解析上下文信息
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("Excel解析完成");
    }

    /**
     * 校验字符串是否为合法日期格式
     *
     * @param str        待校验字符串
     * @param dateFormat 日期格式
     * @return 是否为合法日期
     */
    private boolean isValidDate(String str, String dateFormat) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            sdf.setLenient(false);
            sdf.parse(str);
            return true;
        } catch (ParseException e) {
            log.error("日期格式转换异常", e);
            return false;
        }
    }

    /**
     * 校验字符串是否为数字
     *
     * @param str 待校验字符串
     * @return 是否为数字
     */
    private boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    /**
     * 解析字段对应的列名
     *
     * @param field 字段对象
     * @return 列名
     */
    private String resolveColName(Field field) {
        ExcelProperty prop = field.getAnnotation(ExcelProperty.class);
        if (prop != null && prop.value().length > 0) {
            return prop.value()[prop.value().length - 1];
        }
        return field.getName();
    }
}

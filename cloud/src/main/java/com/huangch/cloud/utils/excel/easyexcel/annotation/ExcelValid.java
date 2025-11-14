package com.huangch.cloud.utils.excel.easyexcel.annotation;

import java.lang.annotation.*;

/**
 * Excel 字段校验注解
 *
 * @author 36020
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelValid {

    /**
     * 是否必填
     */
    boolean required() default false;

    /**
     * 日期格式
     */
    String datePattern() default "";

    /**
     * 是否为数字
     */
    boolean number() default false;
}
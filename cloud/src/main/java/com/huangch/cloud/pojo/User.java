package com.huangch.cloud.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.huangch.cloud.utils.excel.easyexcel.annotation.ExcelValid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author huangch
 * @date 2023-08-16
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @ExcelProperty("姓名")
    @ExcelValid(required = true)
    private String name;

    @ExcelProperty("性别")
    @ExcelValid(required = true)
    private String gender;

    @ExcelProperty("生日")
    @ExcelValid(required = true, datePattern = "yyyy-MM-dd")
    private String birthday;

    @ExcelProperty("薪资")
    private BigDecimal salary;
}

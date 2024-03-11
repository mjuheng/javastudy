package com.huangch.cloud.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author huangch
 * @date 2023-08-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class User {

    @ExcelProperty({"基本信息", "姓名"})
    private String name;

    @ExcelProperty("年龄")
    private Integer age;

    @ExcelProperty("中共党员")
    private Boolean userPartyMember = true;

    private Date createTime;
}

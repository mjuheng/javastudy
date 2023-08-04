package com.huangch.base.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author huangch
 * @date 2023-08-01
 */
@Data
@TableName("students")
public class Student {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    private Integer age;

    private String remark;
}

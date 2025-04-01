package com.huangch.dynamicDatasource.controller;

import com.huangch.base.model.Student;
import com.huangch.base.service.StudentService;
import com.huangch.dynamicDatasource.service.DynamicDataSourceService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author huangch
 * @date 2023-08-01
 */
@RestController
@RequestMapping("/dynamicDatasource")
public class DynamicDatasourceController {

    @Resource
    private DynamicDataSourceService dynamicDataSourceService;
    @Resource
    private StudentService studentService;

    @Transactional(rollbackFor = RuntimeException.class)
    @GetMapping("/insertStudent")
    public void insertStudent(Student student) {
    }
}

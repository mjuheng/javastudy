package com.huangch.dynamicDatasource.service.impl;

import com.huangch.base.mapper.StudentMapper;
import com.huangch.base.model.Student;
import com.huangch.dynamicDatasource.service.DynamicDataSourceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author huangch
 * @date 2023-08-01
 */
@Service
public class DynamicDataSourceServiceImpl implements DynamicDataSourceService {

    @Resource
    private StudentMapper mapper;

    @Override
    public void insertStudent(Student student) {
        mapper.insert(student);
    }
}

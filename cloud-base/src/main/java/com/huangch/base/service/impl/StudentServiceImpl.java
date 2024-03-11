package com.huangch.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huangch.base.mapper.StudentMapper;
import com.huangch.base.model.Student;
import com.huangch.base.service.StudentService;
import org.springframework.stereotype.Service;

/**
 * @author huangch
 * @since 2024-03-04
 */
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {
}

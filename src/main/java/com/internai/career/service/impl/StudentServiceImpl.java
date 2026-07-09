package com.internai.career.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.internai.career.common.BizException;
import com.internai.career.entity.Student;
import com.internai.career.mapper.StudentMapper;
import com.internai.career.security.LoginUserContext;
import com.internai.career.service.StudentService;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {
    @Override
    public Student currentStudent() {
        Student student = getOne(new LambdaQueryWrapper<Student>().eq(Student::getUserId, LoginUserContext.get().userId()), false);
        if (student == null) {
            throw new BizException("当前账号没有绑定学生信息");
        }
        return student;
    }
}

package com.internai.career.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.internai.career.entity.Student;

public interface StudentService extends IService<Student> {
    Student currentStudent();
}

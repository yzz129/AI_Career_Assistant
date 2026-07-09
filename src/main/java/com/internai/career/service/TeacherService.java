package com.internai.career.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.internai.career.entity.Teacher;

public interface TeacherService extends IService<Teacher> {
    Teacher currentTeacher();
}

package com.internai.career.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.internai.career.common.BizException;
import com.internai.career.entity.Teacher;
import com.internai.career.mapper.TeacherMapper;
import com.internai.career.security.LoginUserContext;
import com.internai.career.service.TeacherService;
import org.springframework.stereotype.Service;

@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {
    @Override
    public Teacher currentTeacher() {
        Teacher teacher = getOne(new LambdaQueryWrapper<Teacher>().eq(Teacher::getUserId, LoginUserContext.get().userId()), false);
        if (teacher == null) {
            throw new BizException("当前账号没有绑定教师信息");
        }
        return teacher;
    }
}

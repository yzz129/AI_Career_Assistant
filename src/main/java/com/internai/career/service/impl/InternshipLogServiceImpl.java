package com.internai.career.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.internai.career.common.BizException;
import com.internai.career.dto.LogCommentRequest;
import com.internai.career.entity.InternshipLog;
import com.internai.career.entity.Student;
import com.internai.career.mapper.InternshipLogMapper;
import com.internai.career.security.LoginUserContext;
import com.internai.career.security.RoleGuard;
import com.internai.career.service.InternshipLogService;
import com.internai.career.service.StudentService;
import com.internai.career.service.TeacherService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class InternshipLogServiceImpl extends ServiceImpl<InternshipLogMapper, InternshipLog> implements InternshipLogService {
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final RoleGuard roleGuard;

    public InternshipLogServiceImpl(StudentService studentService, TeacherService teacherService, RoleGuard roleGuard) {
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.roleGuard = roleGuard;
    }

    @Override
    public InternshipLog saveMine(InternshipLog log) {
        roleGuard.require("STUDENT");
        Student student = studentService.currentStudent();
        if (log.getId() != null) {
            InternshipLog old = getById(log.getId());
            if (old == null || !student.getId().equals(old.getStudentId())) {
                throw new BizException("只能维护自己的实习日志");
            }
        }
        log.setStudentId(student.getId());
        log.setTeacherId(student.getTeacherId());
        log.setStatus(log.getStatus() == null ? "DRAFT" : log.getStatus());
        if (log.getCreateTime() == null) {
            log.setCreateTime(LocalDateTime.now());
        }
        log.setUpdateTime(LocalDateTime.now());
        saveOrUpdate(log);
        return log;
    }

    @Override
    public InternshipLog comment(LogCommentRequest request) {
        roleGuard.require("TEACHER", "ADMIN");
        InternshipLog log = getById(request.getLogId());
        if (log == null) {
            throw new BizException("日志不存在");
        }
        if ("TEACHER".equals(LoginUserContext.get().roleCode())) {
            Long currentTeacherId = teacherService.currentTeacher().getId();
            if (!currentTeacherId.equals(log.getTeacherId())) {
                throw new BizException("只能点评自己负责学生的日志");
            }
        }
        log.setTeacherComment(request.getTeacherComment());
        log.setStatus("COMMENTED");
        log.setUpdateTime(LocalDateTime.now());
        updateById(log);
        return log;
    }
}

package com.internai.career.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.internai.career.common.BizException;
import com.internai.career.entity.Resume;
import com.internai.career.entity.Student;
import com.internai.career.mapper.ResumeMapper;
import com.internai.career.security.RoleGuard;
import com.internai.career.service.ResumeService;
import com.internai.career.service.StudentService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ResumeServiceImpl extends ServiceImpl<ResumeMapper, Resume> implements ResumeService {
    private final StudentService studentService;
    private final RoleGuard roleGuard;

    public ResumeServiceImpl(StudentService studentService, RoleGuard roleGuard) {
        this.studentService = studentService;
        this.roleGuard = roleGuard;
    }

    @Override
    public Resume saveMine(Resume resume) {
        roleGuard.require("STUDENT");
        Student student = studentService.currentStudent();
        if (resume.getId() != null) {
            Resume old = getById(resume.getId());
            if (old == null || !student.getId().equals(old.getStudentId())) {
                throw new BizException("只能维护自己的简历");
            }
        }
        resume.setStudentId(student.getId());
        resume.setVersion(resume.getVersion() == null ? 1 : resume.getVersion());
        if (resume.getCreateTime() == null) {
            resume.setCreateTime(LocalDateTime.now());
        }
        resume.setUpdateTime(LocalDateTime.now());
        saveOrUpdate(resume);
        return resume;
    }

    @Override
    public Resume myResume() {
        roleGuard.require("STUDENT");
        Student student = studentService.currentStudent();
        return getOne(new LambdaQueryWrapper<Resume>()
                .eq(Resume::getStudentId, student.getId())
                .orderByDesc(Resume::getUpdateTime)
                .last("limit 1"), false);
    }
}

package com.internai.career.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.internai.career.common.Result;
import com.internai.career.dto.LogCommentRequest;
import com.internai.career.entity.InternshipLog;
import com.internai.career.security.LoginUserContext;
import com.internai.career.security.RoleGuard;
import com.internai.career.service.InternshipLogService;
import com.internai.career.service.StudentService;
import com.internai.career.service.TeacherService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/log")
public class InternshipLogController {
    private final InternshipLogService service;
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final RoleGuard roleGuard;

    public InternshipLogController(InternshipLogService service, StudentService studentService, TeacherService teacherService, RoleGuard roleGuard) {
        this.service = service;
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.roleGuard = roleGuard;
    }

    @PostMapping("/save")
    public Result<InternshipLog> save(@RequestBody InternshipLog log) {
        return Result.ok(service.saveMine(log));
    }

    @PostMapping("/comment")
    public Result<InternshipLog> comment(@Valid @RequestBody LogCommentRequest request) {
        return Result.ok(service.comment(request));
    }

    @GetMapping("/my")
    public Result<Page<InternshipLog>> my(@RequestParam(defaultValue = "1") long pageNum,
                                          @RequestParam(defaultValue = "10") long pageSize) {
        roleGuard.require("STUDENT");
        Long studentId = studentService.currentStudent().getId();
        return Result.ok(service.page(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<InternshipLog>()
                .eq(InternshipLog::getStudentId, studentId)
                .orderByDesc(InternshipLog::getLogDate)));
    }

    @GetMapping("/todo")
    public Result<Page<InternshipLog>> todo(@RequestParam(defaultValue = "1") long pageNum,
                                            @RequestParam(defaultValue = "10") long pageSize) {
        roleGuard.require("TEACHER", "ADMIN");
        LambdaQueryWrapper<InternshipLog> wrapper = new LambdaQueryWrapper<InternshipLog>().orderByDesc(InternshipLog::getLogDate);
        if ("TEACHER".equals(LoginUserContext.get().roleCode())) {
            wrapper.eq(InternshipLog::getTeacherId, teacherService.currentTeacher().getId());
        }
        return Result.ok(service.page(new Page<>(pageNum, pageSize), wrapper));
    }
}

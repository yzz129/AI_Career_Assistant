package com.internai.career.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.internai.career.common.Result;
import com.internai.career.dto.ApplyReviewRequest;
import com.internai.career.dto.ApplySubmitRequest;
import com.internai.career.entity.InternshipApply;
import com.internai.career.security.LoginUserContext;
import com.internai.career.security.RoleGuard;
import com.internai.career.service.InternshipApplyService;
import com.internai.career.service.StudentService;
import com.internai.career.service.TeacherService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/apply")
public class InternshipApplyController {
    private final InternshipApplyService service;
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final RoleGuard roleGuard;

    public InternshipApplyController(InternshipApplyService service, StudentService studentService, TeacherService teacherService, RoleGuard roleGuard) {
        this.service = service;
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.roleGuard = roleGuard;
    }

    @PostMapping("/submit")
    public Result<InternshipApply> submit(@Valid @RequestBody ApplySubmitRequest request) {
        return Result.ok(service.submit(request.getJobId(), request.getReason()));
    }

    @PostMapping("/review")
    public Result<InternshipApply> review(@Valid @RequestBody ApplyReviewRequest request) {
        return Result.ok(service.review(request));
    }

    @GetMapping("/my")
    public Result<Page<InternshipApply>> my(@RequestParam(defaultValue = "1") long pageNum,
                                            @RequestParam(defaultValue = "10") long pageSize) {
        roleGuard.require("STUDENT");
        Long studentId = studentService.currentStudent().getId();
        return Result.ok(service.page(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<InternshipApply>()
                .eq(InternshipApply::getStudentId, studentId)
                .orderByDesc(InternshipApply::getApplyTime)));
    }

    @GetMapping("/todo")
    public Result<Page<InternshipApply>> todo(@RequestParam(defaultValue = "1") long pageNum,
                                              @RequestParam(defaultValue = "10") long pageSize) {
        roleGuard.require("TEACHER", "ADMIN");
        LambdaQueryWrapper<InternshipApply> wrapper = new LambdaQueryWrapper<InternshipApply>().orderByDesc(InternshipApply::getApplyTime);
        if ("TEACHER".equals(LoginUserContext.get().roleCode())) {
            wrapper.eq(InternshipApply::getTeacherId, teacherService.currentTeacher().getId());
        }
        return Result.ok(service.page(new Page<>(pageNum, pageSize), wrapper));
    }
}

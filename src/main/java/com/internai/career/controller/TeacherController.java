package com.internai.career.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.internai.career.common.Result;
import com.internai.career.entity.Teacher;
import com.internai.career.security.RoleGuard;
import com.internai.career.service.TeacherService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {
    private final TeacherService service;
    private final RoleGuard roleGuard;

    public TeacherController(TeacherService service, RoleGuard roleGuard) {
        this.service = service;
        this.roleGuard = roleGuard;
    }

    @GetMapping("/page")
    public Result<Page<Teacher>> page(@RequestParam(name = "pageNum", defaultValue = "1") long pageNum,
                                      @RequestParam(name = "pageSize", defaultValue = "10") long pageSize,
                                      @RequestParam(name = "keyword", required = false) String keyword) {
        roleGuard.require("ADMIN");
        return Result.ok(service.page(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<Teacher>()
                .like(keyword != null && !keyword.isBlank(), Teacher::getName, keyword)
                .or(keyword != null && !keyword.isBlank(), w -> w.like(Teacher::getTeacherNo, keyword))
                .orderByDesc(Teacher::getCreateTime)));
    }

    @GetMapping("/me")
    public Result<Teacher> me() {
        roleGuard.require("TEACHER");
        return Result.ok(service.currentTeacher());
    }

    @GetMapping("/{id}")
    public Result<Teacher> detail(@PathVariable Long id) {
        roleGuard.require("ADMIN");
        return Result.ok(service.getById(id));
    }

    @PostMapping
    public Result<Void> save(@RequestBody Teacher teacher) {
        roleGuard.require("ADMIN");
        service.saveOrUpdate(teacher);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Teacher teacher) {
        roleGuard.require("ADMIN");
        teacher.setId(id);
        service.updateById(teacher);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleGuard.require("ADMIN");
        service.removeById(id);
        return Result.ok();
    }
}

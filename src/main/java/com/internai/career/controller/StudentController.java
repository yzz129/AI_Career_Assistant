package com.internai.career.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.internai.career.common.Result;
import com.internai.career.entity.Student;
import com.internai.career.security.RoleGuard;
import com.internai.career.service.StudentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    private final StudentService service;
    private final RoleGuard roleGuard;

    public StudentController(StudentService service, RoleGuard roleGuard) {
        this.service = service;
        this.roleGuard = roleGuard;
    }

    @GetMapping("/page")
    public Result<Page<Student>> page(@RequestParam(name = "pageNum", defaultValue = "1") long pageNum,
                                      @RequestParam(name = "pageSize", defaultValue = "10") long pageSize,
                                      @RequestParam(name = "keyword", required = false) String keyword) {
        roleGuard.require("ADMIN", "TEACHER");
        return Result.ok(service.page(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<Student>()
                .like(keyword != null && !keyword.isBlank(), Student::getName, keyword)
                .or(keyword != null && !keyword.isBlank(), w -> w.like(Student::getStudentNo, keyword))
                .orderByDesc(Student::getCreateTime)));
    }

    @GetMapping("/me")
    public Result<Student> me() {
        roleGuard.require("STUDENT");
        return Result.ok(service.currentStudent());
    }

    @GetMapping("/{id}")
    public Result<Student> detail(@PathVariable Long id) {
        roleGuard.require("ADMIN", "TEACHER");
        return Result.ok(service.getById(id));
    }

    @PostMapping
    public Result<Void> save(@RequestBody Student student) {
        roleGuard.require("ADMIN");
        service.saveOrUpdate(student);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Student student) {
        roleGuard.require("ADMIN");
        student.setId(id);
        service.updateById(student);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleGuard.require("ADMIN");
        service.removeById(id);
        return Result.ok();
    }
}

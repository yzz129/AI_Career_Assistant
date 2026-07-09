package com.internai.career.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.internai.career.common.Result;
import com.internai.career.entity.JobPosition;
import com.internai.career.security.RoleGuard;
import com.internai.career.service.JobPositionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
public class JobPositionController {
    private final JobPositionService service;
    private final RoleGuard roleGuard;

    public JobPositionController(JobPositionService service, RoleGuard roleGuard) {
        this.service = service;
        this.roleGuard = roleGuard;
    }

    @GetMapping("/page")
    public Result<Page<JobPosition>> page(@RequestParam(defaultValue = "1") long pageNum,
                                          @RequestParam(defaultValue = "10") long pageSize,
                                          @RequestParam(required = false) String title,
                                          @RequestParam(required = false) String city,
                                          @RequestParam(required = false) String skillKeyword,
                                          @RequestParam(required = false) String status) {
        roleGuard.require("ADMIN", "TEACHER", "STUDENT");
        LambdaQueryWrapper<JobPosition> wrapper = new LambdaQueryWrapper<JobPosition>()
                .like(title != null && !title.isBlank(), JobPosition::getTitle, title)
                .like(city != null && !city.isBlank(), JobPosition::getCity, city)
                .like(skillKeyword != null && !skillKeyword.isBlank(), JobPosition::getSkillKeyword, skillKeyword)
                .eq(status != null && !status.isBlank(), JobPosition::getStatus, status)
                .orderByDesc(JobPosition::getCreateTime);
        return Result.ok(service.page(new Page<>(pageNum, pageSize), wrapper));
    }

    @GetMapping("/{id}")
    public Result<JobPosition> detail(@PathVariable Long id) {
        roleGuard.require("ADMIN", "TEACHER", "STUDENT");
        return Result.ok(service.getById(id));
    }

    @PostMapping
    public Result<Void> save(@RequestBody JobPosition job) {
        roleGuard.require("ADMIN");
        service.saveOrUpdate(job);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody JobPosition job) {
        roleGuard.require("ADMIN");
        job.setId(id);
        service.updateById(job);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleGuard.require("ADMIN");
        service.removeById(id);
        return Result.ok();
    }
}

package com.internai.career.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.internai.career.common.Result;
import com.internai.career.entity.Company;
import com.internai.career.security.RoleGuard;
import com.internai.career.service.CompanyService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {
    private final CompanyService service;
    private final RoleGuard roleGuard;

    public CompanyController(CompanyService service, RoleGuard roleGuard) {
        this.service = service;
        this.roleGuard = roleGuard;
    }

    @GetMapping("/page")
    public Result<Page<Company>> page(@RequestParam(name = "pageNum", defaultValue = "1") long pageNum,
                                      @RequestParam(name = "pageSize", defaultValue = "10") long pageSize,
                                      @RequestParam(name = "companyName", required = false) String companyName) {
        roleGuard.require("ADMIN", "TEACHER", "STUDENT");
        return Result.ok(service.page(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<Company>()
                .like(companyName != null && !companyName.isBlank(), Company::getCompanyName, companyName)
                .orderByDesc(Company::getCreateTime)));
    }

    @GetMapping("/{id}")
    public Result<Company> detail(@PathVariable Long id) {
        roleGuard.require("ADMIN", "TEACHER", "STUDENT");
        return Result.ok(service.getById(id));
    }

    @PostMapping
    public Result<Void> save(@RequestBody Company company) {
        roleGuard.require("ADMIN");
        service.saveOrUpdate(company);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Company company) {
        roleGuard.require("ADMIN");
        company.setId(id);
        service.updateById(company);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleGuard.require("ADMIN");
        service.removeById(id);
        return Result.ok();
    }
}

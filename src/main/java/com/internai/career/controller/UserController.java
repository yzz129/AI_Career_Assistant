package com.internai.career.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.internai.career.common.Result;
import com.internai.career.entity.SysUser;
import com.internai.career.security.RoleGuard;
import com.internai.career.service.SysUserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final SysUserService service;
    private final RoleGuard roleGuard;

    public UserController(SysUserService service, RoleGuard roleGuard) {
        this.service = service;
        this.roleGuard = roleGuard;
    }

    @GetMapping("/page")
    public Result<Page<SysUser>> page(@RequestParam(name = "pageNum", defaultValue = "1") long pageNum,
                                      @RequestParam(name = "pageSize", defaultValue = "10") long pageSize,
                                      @RequestParam(name = "keyword", required = false) String keyword,
                                      @RequestParam(name = "roleCode", required = false) String roleCode) {
        roleGuard.require("ADMIN");
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .like(keyword != null && !keyword.isBlank(), SysUser::getUsername, keyword)
                .or(keyword != null && !keyword.isBlank(), w -> w.like(SysUser::getRealName, keyword))
                .eq(roleCode != null && !roleCode.isBlank(), SysUser::getRoleCode, roleCode)
                .orderByDesc(SysUser::getCreateTime);
        Page<SysUser> page = service.page(new Page<>(pageNum, pageSize), wrapper);
        page.getRecords().forEach(user -> user.setPassword(null));
        return Result.ok(page);
    }

    @GetMapping("/{id}")
    public Result<SysUser> detail(@PathVariable Long id) {
        roleGuard.require("ADMIN");
        SysUser user = service.getById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.ok(user);
    }

    @PostMapping
    public Result<Void> save(@RequestBody SysUser user) {
        roleGuard.require("ADMIN");
        service.saveWithEncodedPassword(user);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysUser user) {
        roleGuard.require("ADMIN");
        user.setId(id);
        service.saveWithEncodedPassword(user);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleGuard.require("ADMIN");
        service.removeById(id);
        return Result.ok();
    }
}

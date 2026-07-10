package com.internai.career.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.internai.career.common.Result;
import com.internai.career.entity.KnowledgeDoc;
import com.internai.career.security.RoleGuard;
import com.internai.career.service.KnowledgeDocService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeDocController {
    private final KnowledgeDocService service;
    private final RoleGuard roleGuard;

    public KnowledgeDocController(KnowledgeDocService service, RoleGuard roleGuard) {
        this.service = service;
        this.roleGuard = roleGuard;
    }

    @GetMapping("/page")
    public Result<Page<KnowledgeDoc>> page(@RequestParam(name = "pageNum", defaultValue = "1") long pageNum,
                                           @RequestParam(name = "pageSize", defaultValue = "10") long pageSize,
                                           @RequestParam(name = "keyword", required = false) String keyword) {
        roleGuard.require("ADMIN", "TEACHER", "STUDENT");
        return Result.ok(service.page(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<KnowledgeDoc>()
                .like(keyword != null && !keyword.isBlank(), KnowledgeDoc::getTitle, keyword)
                .or(keyword != null && !keyword.isBlank(), w -> w.like(KnowledgeDoc::getKeywords, keyword))
                .orderByDesc(KnowledgeDoc::getCreateTime)));
    }

    @GetMapping("/{id}")
    public Result<KnowledgeDoc> detail(@PathVariable Long id) {
        roleGuard.require("ADMIN", "TEACHER", "STUDENT");
        return Result.ok(service.getById(id));
    }

    @PostMapping
    public Result<Void> save(@RequestBody KnowledgeDoc doc) {
        roleGuard.require("ADMIN");
        service.saveOrUpdate(doc);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody KnowledgeDoc doc) {
        roleGuard.require("ADMIN");
        doc.setId(id);
        service.updateById(doc);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleGuard.require("ADMIN");
        service.removeById(id);
        return Result.ok();
    }
}

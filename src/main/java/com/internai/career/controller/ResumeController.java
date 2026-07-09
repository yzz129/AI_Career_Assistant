package com.internai.career.controller;

import com.internai.career.common.Result;
import com.internai.career.entity.Resume;
import com.internai.career.service.ResumeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resume")
public class ResumeController {
    private final ResumeService service;

    public ResumeController(ResumeService service) {
        this.service = service;
    }

    @PostMapping("/save")
    public Result<Resume> save(@RequestBody Resume resume) {
        return Result.ok(service.saveMine(resume));
    }

    @GetMapping("/my")
    public Result<Resume> my() {
        return Result.ok(service.myResume());
    }
}

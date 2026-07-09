package com.internai.career.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.internai.career.entity.Resume;

public interface ResumeService extends IService<Resume> {
    Resume saveMine(Resume resume);
    Resume myResume();
}

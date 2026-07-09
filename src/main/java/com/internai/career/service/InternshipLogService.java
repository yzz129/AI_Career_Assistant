package com.internai.career.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.internai.career.dto.LogCommentRequest;
import com.internai.career.entity.InternshipLog;

public interface InternshipLogService extends IService<InternshipLog> {
    InternshipLog saveMine(InternshipLog log);
    InternshipLog comment(LogCommentRequest request);
}

package com.internai.career.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.internai.career.dto.ApplyReviewRequest;
import com.internai.career.entity.InternshipApply;

public interface InternshipApplyService extends IService<InternshipApply> {
    InternshipApply submit(Long jobId, String reason);
    InternshipApply review(ApplyReviewRequest request);
}

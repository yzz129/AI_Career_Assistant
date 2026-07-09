package com.internai.career.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApplyReviewRequest {
    @NotNull
    private Long applyId;
    @NotBlank
    private String status;
    private String reviewComment;
}

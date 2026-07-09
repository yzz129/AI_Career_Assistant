package com.internai.career.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApplySubmitRequest {
    @NotNull
    private Long jobId;
    private String reason;
}

package com.internai.career.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LogCommentRequest {
    @NotNull
    private Long logId;
    private String teacherComment;
}

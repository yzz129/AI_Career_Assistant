package com.internai.career.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("internship_apply")
public class InternshipApply {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private Long teacherId;
    private Long jobId;
    private String jobTitle;
    private String companyName;
    private String reason;
    private String status;
    private String reviewComment;
    private LocalDateTime applyTime;
    private LocalDateTime reviewTime;
}

package com.internai.career.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("internship_log")
public class InternshipLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private Long teacherId;
    private LocalDate logDate;
    private String content;
    private String status;
    private String teacherComment;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

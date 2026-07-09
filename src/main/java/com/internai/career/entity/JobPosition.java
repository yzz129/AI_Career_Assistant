package com.internai.career.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("job_position")
public class JobPosition {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long companyId;
    private String companyName;
    private String title;
    private String city;
    private String salaryRange;
    private String skillKeyword;
    private String description;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

package com.internai.career.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.internai.career.entity.JobPosition;
import com.internai.career.mapper.JobPositionMapper;
import com.internai.career.service.JobPositionService;
import org.springframework.stereotype.Service;

@Service
public class JobPositionServiceImpl extends ServiceImpl<JobPositionMapper, JobPosition> implements JobPositionService {
}

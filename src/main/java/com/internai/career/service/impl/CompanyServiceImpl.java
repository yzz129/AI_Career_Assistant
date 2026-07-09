package com.internai.career.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.internai.career.entity.Company;
import com.internai.career.mapper.CompanyMapper;
import com.internai.career.service.CompanyService;
import org.springframework.stereotype.Service;

@Service
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company> implements CompanyService {
}

package com.internai.career.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.internai.career.entity.SysRole;
import com.internai.career.mapper.SysRoleMapper;
import com.internai.career.service.SysRoleService;
import org.springframework.stereotype.Service;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
}

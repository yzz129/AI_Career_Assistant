package com.internai.career.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.internai.career.entity.SysUser;
import com.internai.career.mapper.SysUserMapper;
import com.internai.career.service.SysUserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public void saveWithEncodedPassword(SysUser user) {
        if (user.getId() == null) {
            user.setCreateTime(LocalDateTime.now());
        }
        user.setUpdateTime(LocalDateTime.now());
        if (user.getStatus() == null) {
            user.setStatus("ENABLED");
        }
        if (user.getPassword() != null && !user.getPassword().startsWith("$2")) {
            user.setPassword(encoder.encode(user.getPassword()));
        }
        saveOrUpdate(user);
    }
}

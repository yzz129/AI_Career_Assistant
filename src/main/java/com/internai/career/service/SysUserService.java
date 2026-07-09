package com.internai.career.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.internai.career.entity.SysUser;

public interface SysUserService extends IService<SysUser> {
    void saveWithEncodedPassword(SysUser user);
}

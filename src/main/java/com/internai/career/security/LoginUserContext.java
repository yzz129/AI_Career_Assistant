package com.internai.career.security;

import com.internai.career.common.BizException;
import com.internai.career.common.ResultCode;

public class LoginUserContext {
    private static final ThreadLocal<LoginUser> HOLDER = new ThreadLocal<>();

    private LoginUserContext() {
    }

    public static void set(LoginUser user) {
        HOLDER.set(user);
    }

    public static LoginUser get() {
        LoginUser user = HOLDER.get();
        if (user == null) {
            throw new BizException(ResultCode.UNAUTHORIZED, "登录状态已失效");
        }
        return user;
    }

    public static void clear() {
        HOLDER.remove();
    }
}

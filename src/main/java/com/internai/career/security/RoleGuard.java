package com.internai.career.security;

import com.internai.career.common.BizException;
import com.internai.career.common.ResultCode;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class RoleGuard {
    public void require(String... roles) {
        String roleCode = LoginUserContext.get().roleCode();
        boolean matched = Arrays.stream(roles).anyMatch(roleCode::equals);
        if (!matched) {
            throw new BizException(ResultCode.FORBIDDEN, "当前角色无权访问该功能");
        }
    }
}

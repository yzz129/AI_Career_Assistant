package com.internai.career.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.internai.career.common.BizException;
import com.internai.career.common.Result;
import com.internai.career.dto.LoginRequest;
import com.internai.career.entity.SysUser;
import com.internai.career.security.JwtUtil;
import com.internai.career.security.LoginUser;
import com.internai.career.security.LoginUserContext;
import com.internai.career.service.SysUserService;
import com.internai.career.vo.LoginVO;
import jakarta.validation.Valid;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final SysUserService userService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthController(SysUserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        SysUser user = userService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, request.getUsername()), false);
        if (user == null || !"ENABLED".equals(user.getStatus()) || !passwordMatches(request.getPassword(), user)) {
            throw new BizException("用户名或密码错误");
        }
        LoginUser loginUser = new LoginUser(user.getId(), user.getUsername(), user.getRealName(), user.getRoleCode());
        String token = jwtUtil.createToken(loginUser);
        return Result.ok(new LoginVO(token, user.getId(), user.getUsername(), user.getRealName(), user.getRoleCode()));
    }

    private boolean passwordMatches(String rawPassword, SysUser user) {
        String stored = user.getPassword();
        if (stored != null && stored.startsWith("$2")) {
            return encoder.matches(rawPassword, stored);
        }
        boolean matched = rawPassword.equals(stored);
        if (matched) {
            user.setPassword(encoder.encode(rawPassword));
            userService.updateById(user);
        }
        return matched;
    }

    @GetMapping("/me")
    public Result<LoginUser> me() {
        return Result.ok(LoginUserContext.get());
    }
}

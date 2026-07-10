package com.internai.career.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.internai.career.common.BizException;
import com.internai.career.common.Result;
import com.internai.career.dto.LoginRequest;
import com.internai.career.dto.RegisterRequest;
import com.internai.career.entity.Student;
import com.internai.career.entity.SysUser;
import com.internai.career.entity.Teacher;
import com.internai.career.security.JwtUtil;
import com.internai.career.security.LoginUser;
import com.internai.career.security.LoginUserContext;
import com.internai.career.service.SysUserService;
import com.internai.career.service.StudentService;
import com.internai.career.service.TeacherService;
import com.internai.career.vo.LoginVO;
import jakarta.validation.Valid;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final SysUserService userService;
    private final JwtUtil jwtUtil;
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthController(SysUserService userService, JwtUtil jwtUtil, StudentService studentService, TeacherService teacherService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.studentService = studentService;
        this.teacherService = teacherService;
    }

    @PostMapping("/register")
    @Transactional
    public Result<LoginVO> register(@Valid @RequestBody RegisterRequest request) {
        String roleCode = request.getRoleCode().trim().toUpperCase();
        if (!"STUDENT".equals(roleCode) && !"TEACHER".equals(roleCode)) {
            throw new BizException("注册身份只能选择学生或教师");
        }
        long existing = userService.count(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, request.getUsername().trim()));
        if (existing > 0) {
            throw new BizException("用户名已存在");
        }

        SysUser user = new SysUser();
        user.setUsername(request.getUsername().trim());
        user.setPassword(request.getPassword());
        user.setRealName(request.getRealName().trim());
        user.setRoleCode(roleCode);
        user.setPhone(blankToNull(request.getPhone()));
        user.setEmail(blankToNull(request.getEmail()));
        user.setStatus("ENABLED");
        userService.saveWithEncodedPassword(user);

        if ("STUDENT".equals(roleCode)) {
            Student student = new Student();
            student.setUserId(user.getId());
            student.setStudentNo("S" + String.format("%07d", user.getId()));
            student.setName(user.getRealName());
            studentService.save(student);
        } else {
            Teacher teacher = new Teacher();
            teacher.setUserId(user.getId());
            teacher.setTeacherNo("T" + String.format("%07d", user.getId()));
            teacher.setName(user.getRealName());
            teacher.setPhone(user.getPhone());
            teacherService.save(teacher);
        }

        LoginUser loginUser = new LoginUser(user.getId(), user.getUsername(), user.getRealName(), roleCode);
        String token = jwtUtil.createToken(loginUser);
        return Result.ok(new LoginVO(token, user.getId(), user.getUsername(), user.getRealName(), roleCode));
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
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

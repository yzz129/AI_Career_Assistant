package com.internai.career.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[A-Za-z0-9_]{4,32}$", message = "用户名只能包含字母、数字和下划线，长度为 4-32 位")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度应为 6-32 位")
    private String password;

    @NotBlank(message = "姓名不能为空")
    @Size(max = 64, message = "姓名不能超过 64 个字符")
    private String realName;

    @NotBlank(message = "请选择身份")
    private String roleCode;

    @Size(max = 32, message = "手机号不能超过 32 个字符")
    private String phone;

    @Email(message = "邮箱格式不正确")
    @Size(max = 120, message = "邮箱不能超过 120 个字符")
    private String email;
}

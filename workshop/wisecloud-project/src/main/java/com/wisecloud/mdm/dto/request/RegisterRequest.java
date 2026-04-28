package com.wisecloud.mdm.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "用户名不能为空")
        String username,

        @NotBlank(message = "邮箱不能为空")
        @Email(message = "邮箱格式错误")
        String email,

        @NotBlank(message = "密码不能为空")
        @Size(min = 8, message = "密码长度不足")
        String password
) {}

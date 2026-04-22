package com.hnhegui.hc.controller.cuser.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    /**
     * 账号（手机号/邮箱/用户名）
     */
    @NotBlank(message = "账号不能为空")
    private String account;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}

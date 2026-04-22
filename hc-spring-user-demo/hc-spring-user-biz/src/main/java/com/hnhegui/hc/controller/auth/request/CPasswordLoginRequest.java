package com.hnhegui.hc.controller.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CPasswordLoginRequest {

    /**
     * 账号（手机号/邮箱/用户名）
     */
    @NotBlank(message = "账号不能为空")
    private String account;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}

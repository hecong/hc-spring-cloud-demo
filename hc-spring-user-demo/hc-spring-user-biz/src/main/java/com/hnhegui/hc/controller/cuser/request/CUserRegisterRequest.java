package com.hnhegui.hc.controller.cuser.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CUserRegisterRequest {

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}

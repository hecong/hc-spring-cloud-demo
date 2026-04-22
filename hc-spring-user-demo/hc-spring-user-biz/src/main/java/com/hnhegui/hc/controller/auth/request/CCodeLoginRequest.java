package com.hnhegui.hc.controller.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CCodeLoginRequest {

    /**
     * 发送目标（手机号/邮箱）
     */
    @NotBlank(message = "目标不能为空")
    private String target;

    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    private String code;
}

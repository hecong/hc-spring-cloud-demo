package com.hnhegui.hc.controller.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BPasswordLoginRequest {

    /**
     * 企业编码
     */
    @NotBlank(message = "企业编码不能为空")
    private String enterpriseCode;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}

package com.hnhegui.hc.controller.cuser.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SetIdentityDefaultRequest {

    /**
     * 默认登录身份
     */
    @NotBlank(message = "默认身份不能为空")
    private String identityDefault;
}

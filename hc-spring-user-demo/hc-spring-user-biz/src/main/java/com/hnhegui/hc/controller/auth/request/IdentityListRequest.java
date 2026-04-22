package com.hnhegui.hc.controller.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class IdentityListRequest {

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    private String phone;
}

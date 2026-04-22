package com.hnhegui.hc.controller.enterprise.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetEnterpriseUserPasswordRequest {

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}

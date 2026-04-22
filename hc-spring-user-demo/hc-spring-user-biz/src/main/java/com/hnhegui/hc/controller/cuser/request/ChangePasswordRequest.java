package com.hnhegui.hc.controller.cuser.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    /**
     * 原密码
     */
    @NotBlank(message = "原密码不能为空")
    private String oldPassword;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}

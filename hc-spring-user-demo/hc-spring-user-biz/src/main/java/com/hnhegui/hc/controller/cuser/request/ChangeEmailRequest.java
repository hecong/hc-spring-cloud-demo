package com.hnhegui.hc.controller.cuser.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangeEmailRequest {

    /**
     * 新邮箱
     */
    @NotBlank(message = "新邮箱不能为空")
    private String newEmail;
}

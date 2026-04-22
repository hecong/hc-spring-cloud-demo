package com.hnhegui.hc.controller.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendCodeRequest {

    /**
     * 发送目标（手机号/邮箱）
     */
    @NotBlank(message = "目标不能为空")
    private String target;

    /**
     * 场景：login/register/reset
     */
    @NotBlank(message = "场景不能为空")
    private String scene;
}

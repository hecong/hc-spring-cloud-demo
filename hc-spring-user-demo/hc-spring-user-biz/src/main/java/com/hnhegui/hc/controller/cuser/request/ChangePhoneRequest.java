package com.hnhegui.hc.controller.cuser.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePhoneRequest {

    /**
     * 新手机号
     */
    @NotBlank(message = "新手机号不能为空")
    private String newPhone;
}

package com.hnhegui.hc.controller.cuser.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UnbindThirdPartyRequest {

    /**
     * 第三方平台：wechat/alipay/qq
     */
    @NotBlank(message = "平台不能为空")
    private String platform;
}

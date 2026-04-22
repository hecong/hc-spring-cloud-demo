package com.hnhegui.hc.controller.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SelectIdentityRequest {

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    private String phone;

    /**
     * 身份类型：C-个人，B-企业
     */
    @NotBlank(message = "身份类型不能为空")
    private String identityType;

    /**
     * 企业ID（B端身份时必填）
     */
    @NotNull(message = "企业ID不能为空", groups = BIdentity.class)
    private Long enterpriseId;

    /**
     * B端身份校验分组
     */
    public interface BIdentity {}
}

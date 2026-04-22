package com.hnhegui.hc.controller.auth.response;

import lombok.Data;

@Data
public class IdentityItemResponse {

    /**
     * 身份类型：C-个人，B-企业
     */
    private String identityType;

    /**
     * 身份标签
     */
    private String label;

    /**
     * 企业ID
     */
    private Long enterpriseId;

    /**
     * 企业编码
     */
    private String enterpriseCode;

    /**
     * 用户ID
     */
    private Long userId;
}

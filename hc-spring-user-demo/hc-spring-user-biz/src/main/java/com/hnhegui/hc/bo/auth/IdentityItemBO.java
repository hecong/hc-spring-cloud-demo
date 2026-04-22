package com.hnhegui.hc.bo.auth;

import lombok.Data;

@Data
public class IdentityItemBO {

    /**
     * 身份类型：C-个人，B-企业
     */
    private String identityType;

    /**
     * 身份标签（个人中心/企业名）
     */
    private String label;

    /**
     * 企业ID（B端时）
     */
    private Long enterpriseId;

    /**
     * 企业编码（B端时）
     */
    private String enterpriseCode;

    /**
     * 用户ID
     */
    private Long userId;
}

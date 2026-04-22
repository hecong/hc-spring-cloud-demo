package com.hnhegui.hc.controller.auth.response;

import lombok.Data;

import java.util.List;

@Data
public class LoginResponse {

    /**
     * 登录token
     */
    private String token;

    /**
     * token名称
     */
    private String tokenName;

    /**
     * 用户类型：C/B/P
     */
    private String userType;

    /**
     * 是否需要选择身份
     */
    private Boolean needSelectIdentity;

    /**
     * 手机号（多身份选择时用）
     */
    private String phone;

    /**
     * 企业ID（B端登录时）
     */
    private Long enterpriseId;

    /**
     * 企业编码（B端登录时）
     */
    private String enterpriseCode;

    /**
     * 企业名称（B端登录时）
     */
    private String enterpriseName;

    /**
     * 是否首次登录（B端）
     */
    private Integer isFirstLogin;

    /**
     * C端用户信息
     */
    private CUserLoginInfoResponse cUserInfo;

    /**
     * B端用户信息
     */
    private EnterpriseUserLoginInfoResponse bUserInfo;

    /**
     * 身份列表（多身份选择时用）
     */
    private List<IdentityItemResponse> identityList;

    /**
     * 默认身份
     */
    private String identityDefault;
}

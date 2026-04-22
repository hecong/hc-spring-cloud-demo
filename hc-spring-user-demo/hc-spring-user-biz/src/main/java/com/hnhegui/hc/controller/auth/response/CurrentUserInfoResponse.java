package com.hnhegui.hc.controller.auth.response;

import lombok.Data;

import java.util.List;

@Data
public class CurrentUserInfoResponse {

    /**
     * 用户类型：C/B/P
     */
    private String userType;

    /**
     * 企业ID（B端时）
     */
    private Long enterpriseId;

    /**
     * 角色列表
     */
    private List<String> roles;

    /**
     * 权限列表
     */
    private List<String> permissions;

    /**
     * C端用户信息
     */
    private CUserLoginInfoResponse cUserInfo;

    /**
     * B端用户信息
     */
    private EnterpriseUserLoginInfoResponse bUserInfo;
}

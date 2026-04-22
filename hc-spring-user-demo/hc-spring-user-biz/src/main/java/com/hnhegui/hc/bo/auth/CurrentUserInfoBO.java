package com.hnhegui.hc.bo.auth;

import lombok.Data;

import java.util.List;

@Data
public class CurrentUserInfoBO {

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
    private CUserLoginInfoBO cUserInfo;

    /**
     * B端用户信息
     */
    private EnterpriseUserLoginInfoBO bUserInfo;
}

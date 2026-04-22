package com.hnhegui.hc.controller.auth.response;

import lombok.Data;

@Data
public class EnterpriseUserLoginInfoResponse {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 企业ID
     */
    private Long enterpriseId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 姓名
     */
    private String name;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 是否首次登录
     */
    private Integer isFirstLogin;

    /**
     * 企业名称
     */
    private String enterpriseName;

    /**
     * 企业编码
     */
    private String enterpriseCode;
}

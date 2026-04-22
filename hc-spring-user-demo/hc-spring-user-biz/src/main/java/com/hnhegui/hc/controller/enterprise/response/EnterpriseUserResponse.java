package com.hnhegui.hc.controller.enterprise.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EnterpriseUserResponse {

    /**
     * ID
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
     * 激活有效期
     */
    private LocalDateTime activationExpireTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

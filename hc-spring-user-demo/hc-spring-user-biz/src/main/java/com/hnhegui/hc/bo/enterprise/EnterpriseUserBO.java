package com.hnhegui.hc.bo.enterprise;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EnterpriseUserBO {

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
     * 密码(BCrypt)
     */
    private String password;

    /**
     * 状态：1-正常，2-锁定，3-禁用，4-未激活，5-已离职
     */
    private Integer status;

    /**
     * 密码连续错误次数
     */
    private Integer passwordErrorCount;

    /**
     * 账号锁定时间
     */
    private LocalDateTime lockTime;

    /**
     * 是否首次登录：0-否，1-是
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

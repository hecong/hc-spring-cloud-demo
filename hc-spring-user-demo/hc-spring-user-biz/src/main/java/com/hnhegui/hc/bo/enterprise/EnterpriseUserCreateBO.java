package com.hnhegui.hc.bo.enterprise;

import lombok.Data;

@Data
public class EnterpriseUserCreateBO {

    /**
     * 企业ID
     */
    private Long enterpriseId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

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
     * 新密码（用于修改/重置密码场景）
     */
    private String newPassword;

    /**
     * 原密码（用于修改密码场景）
     */
    private String oldPassword;
}

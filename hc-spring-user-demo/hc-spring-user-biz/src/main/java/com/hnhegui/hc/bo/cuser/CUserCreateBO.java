package com.hnhegui.hc.bo.cuser;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CUserCreateBO {

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 登录账号（用于找回密码等场景）
     */
    private String account;

    /**
     * 新密码（用于修改/重置密码场景）
     */
    private String newPassword;

    /**
     * 原密码（用于修改密码场景）
     */
    private String oldPassword;

    /**
     * 默认登录身份
     */
    private String identityDefault;
}

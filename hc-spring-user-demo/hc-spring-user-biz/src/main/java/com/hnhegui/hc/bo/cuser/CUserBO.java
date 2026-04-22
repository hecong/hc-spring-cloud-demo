package com.hnhegui.hc.bo.cuser;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CUserBO {

    /**
     * ID
     */
    private Long id;

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
     * 密码(BCrypt)
     */
    private String password;

    /**
     * 状态：1-正常，2-锁定，3-禁用
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
     * 是否开启免密登录：0-否，1-是
     */
    private Integer rememberLogin;

    /**
     * 默认登录身份
     */
    private String identityDefault;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

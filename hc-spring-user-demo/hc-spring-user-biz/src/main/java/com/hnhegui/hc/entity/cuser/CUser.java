package com.hnhegui.hc.entity.cuser;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hc.framework.mybatis.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("c_user")
public class CUser extends BaseEntity {

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
     * 密码(BCrypt)
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
     * 默认登录身份(JSON存储)
     */
    private String identityDefault;
}

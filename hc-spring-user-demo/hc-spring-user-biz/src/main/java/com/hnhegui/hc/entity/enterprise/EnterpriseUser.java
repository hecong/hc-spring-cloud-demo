package com.hnhegui.hc.entity.enterprise;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hc.framework.mybatis.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("biz_enterprise_user")
public class EnterpriseUser extends BaseEntity {

    /**
     * 企业ID
     */
    private Long enterpriseId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码(BCrypt)
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
     * 激活有效期(7天)
     */
    private LocalDateTime activationExpireTime;
}

package com.hnhegui.hc.entity.log;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hc.framework.mybatis.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_login_log")
public class LoginLog extends BaseEntity {

    /**
     * 用户类型：C-C端，B-B端，P-平台
     */
    private String userType;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 登录账号
     */
    private String account;

    /**
     * 登录时间
     */
    private LocalDateTime loginTime;

    /**
     * 登录IP
     */
    private String loginIp;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 登录设备信息
     */
    private String loginDevice;

    /**
     * 登录状态：1-成功，0-失败
     */
    private Integer loginStatus;

    /**
     * 失败原因
     */
    private String failReason;
}

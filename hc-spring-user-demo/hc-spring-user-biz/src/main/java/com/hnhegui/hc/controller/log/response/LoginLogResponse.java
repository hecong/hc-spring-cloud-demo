package com.hnhegui.hc.controller.log.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LoginLogResponse {

    /**
     * ID
     */
    private Long id;

    /**
     * 用户类型
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

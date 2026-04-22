package com.hnhegui.hc.controller.enterprise.request;

import lombok.Data;

@Data
public class SecuritySettingRequest {

    /**
     * IP白名单
     */
    private String ipWhitelist;

    /**
     * 是否开启登录互踢：0-否，1-是
     */
    private Integer loginMutualExclusion;

    /**
     * 登录密码规则
     */
    private String passwordRule;
}

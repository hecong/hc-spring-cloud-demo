package com.hnhegui.hc.common.enums;

import lombok.Getter;

/**
 * 第三方平台枚举
 */
@Getter
public enum ThirdPartyPlatformEnum {

    /**
     * 微信
     */
    WECHAT("wechat", "微信"),

    /**
     * 支付宝
     */
    ALIPAY("alipay", "支付宝"),

    /**
     * QQ
     */
    QQ("qq", "QQ");

    private final String code;
    private final String desc;

    ThirdPartyPlatformEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据code获取枚举
     */
    public static ThirdPartyPlatformEnum getByCode(String code) {
        for (ThirdPartyPlatformEnum platform : values()) {
            if (platform.code.equals(code)) {
                return platform;
            }
        }
        return null;
    }
}

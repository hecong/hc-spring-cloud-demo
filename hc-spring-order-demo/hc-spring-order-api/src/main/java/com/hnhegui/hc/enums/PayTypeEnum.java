package com.hnhegui.hc.enums;

import lombok.Getter;

/**
 * 支付方式枚举
 */
@Getter
public enum PayTypeEnum {

    /**
     * 未知
     */
    UNKNOWN("UNKNOWN", "未知"),

    /**
     * 微信
     */
    WECHAT("WECHAT", "微信"),

    /**
     * 支付宝
     */
    ALIPAY("ALIPAY", "支付宝");

    private final String code;
    private final String desc;

    PayTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据code获取枚举
     */
    public static PayTypeEnum getByCode(String code) {
        for (PayTypeEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}

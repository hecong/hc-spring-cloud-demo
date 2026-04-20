package com.hnhegui.hc.enums;

import lombok.Getter;

/**
 * 支付状态枚举
 */
@Getter
public enum PayStatusEnum {

    /**
     * 待支付
     */
    PENDING("PENDING", "待支付"),

    /**
     * 支付成功
     */
    SUCCESS("SUCCESS", "支付成功"),

    /**
     * 支付失败
     */
    FAIL("FAIL", "支付失败");

    private final String code;
    private final String desc;

    PayStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据code获取枚举
     */
    public static PayStatusEnum getByCode(String code) {
        for (PayStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}

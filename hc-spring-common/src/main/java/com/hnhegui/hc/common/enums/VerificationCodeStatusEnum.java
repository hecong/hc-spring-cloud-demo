package com.hnhegui.hc.common.enums;

import lombok.Getter;

/**
 * 验证码状态枚举
 */
@Getter
public enum VerificationCodeStatusEnum {

    /**
     * 有效
     */
    VALID(1, "有效"),

    /**
     * 已使用
     */
    USED(2, "已使用"),

    /**
     * 已过期
     */
    EXPIRED(3, "已过期");

    private final int code;
    private final String desc;

    VerificationCodeStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据code获取枚举
     */
    public static VerificationCodeStatusEnum getByCode(int code) {
        for (VerificationCodeStatusEnum status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}

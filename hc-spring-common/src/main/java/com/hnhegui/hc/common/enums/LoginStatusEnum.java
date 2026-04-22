package com.hnhegui.hc.common.enums;

import lombok.Getter;

/**
 * 登录状态枚举
 */
@Getter
public enum LoginStatusEnum {

    /**
     * 失败
     */
    FAIL(0, "失败"),

    /**
     * 成功
     */
    SUCCESS(1, "成功");

    private final int code;
    private final String desc;

    LoginStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据code获取枚举
     */
    public static LoginStatusEnum getByCode(int code) {
        for (LoginStatusEnum status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}

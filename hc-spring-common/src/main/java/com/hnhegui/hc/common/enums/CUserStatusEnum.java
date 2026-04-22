package com.hnhegui.hc.common.enums;

import lombok.Getter;

/**
 * C端用户状态枚举
 */
@Getter
public enum CUserStatusEnum {

    /**
     * 正常
     */
    NORMAL(1, "正常"),

    /**
     * 锁定
     */
    LOCKED(2, "锁定"),

    /**
     * 禁用
     */
    DISABLED(3, "禁用");

    private final int code;
    private final String desc;

    CUserStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据code获取枚举
     */
    public static CUserStatusEnum getByCode(int code) {
        for (CUserStatusEnum status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}

package com.hnhegui.hc.common.enums;

import lombok.Getter;

/**
 * 账号锁定状态枚举
 */
@Getter
public enum AccountLockStatusEnum {

    /**
     * 锁定中
     */
    LOCKED(1, "锁定中"),

    /**
     * 已解锁
     */
    UNLOCKED(2, "已解锁");

    private final int code;
    private final String desc;

    AccountLockStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据code获取枚举
     */
    public static AccountLockStatusEnum getByCode(int code) {
        for (AccountLockStatusEnum status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}

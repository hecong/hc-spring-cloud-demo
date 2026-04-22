package com.hnhegui.hc.common.enums;

import lombok.Getter;

/**
 * 账号锁定原因枚举
 */
@Getter
public enum LockReasonEnum {

    /**
     * 密码错误
     */
    PASSWORD_ERROR("password_error", "密码错误"),

    /**
     * 异常登录
     */
    ABNORMAL_LOGIN("abnormal_login", "异常登录");

    private final String code;
    private final String desc;

    LockReasonEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据code获取枚举
     */
    public static LockReasonEnum getByCode(String code) {
        for (LockReasonEnum reason : values()) {
            if (reason.code.equals(code)) {
                return reason;
            }
        }
        return null;
    }
}

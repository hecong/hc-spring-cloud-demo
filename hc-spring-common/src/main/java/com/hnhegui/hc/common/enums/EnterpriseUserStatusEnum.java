package com.hnhegui.hc.common.enums;

import lombok.Getter;

/**
 * B端企业用户状态枚举
 */
@Getter
public enum EnterpriseUserStatusEnum {

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
    DISABLED(3, "禁用"),

    /**
     * 未激活
     */
    INACTIVE(4, "未激活"),

    /**
     * 已离职
     */
    RESIGNED(5, "已离职");

    private final int code;
    private final String desc;

    EnterpriseUserStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据code获取枚举
     */
    public static EnterpriseUserStatusEnum getByCode(int code) {
        for (EnterpriseUserStatusEnum status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}

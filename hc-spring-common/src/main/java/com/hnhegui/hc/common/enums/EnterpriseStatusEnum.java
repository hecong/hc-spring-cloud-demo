package com.hnhegui.hc.common.enums;

import lombok.Getter;

/**
 * 企业状态枚举
 */
@Getter
public enum EnterpriseStatusEnum {

    /**
     * 正常
     */
    NORMAL(1, "正常"),

    /**
     * 过期
     */
    EXPIRED(2, "过期"),

    /**
     * 禁用
     */
    DISABLED(3, "禁用");

    private final int code;
    private final String desc;

    EnterpriseStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据code获取枚举
     */
    public static EnterpriseStatusEnum getByCode(int code) {
        for (EnterpriseStatusEnum status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}

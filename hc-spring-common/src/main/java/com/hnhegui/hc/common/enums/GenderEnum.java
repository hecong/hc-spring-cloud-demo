package com.hnhegui.hc.common.enums;

import lombok.Getter;

/**
 * 性别枚举
 */
@Getter
public enum GenderEnum {

    /**
     * 未知
     */
    UNKNOWN(0, "未知"),

    /**
     * 男
     */
    MALE(1, "男"),

    /**
     * 女
     */
    FEMALE(2, "女");

    private final int code;
    private final String desc;

    GenderEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据code获取枚举
     */
    public static GenderEnum getByCode(int code) {
        for (GenderEnum gender : values()) {
            if (gender.code == code) {
                return gender;
            }
        }
        return null;
    }
}

package com.hnhegui.hc.common.enums;

import lombok.Getter;

/**
 * 用户类型枚举
 */
@Getter
public enum UserTypeEnum {

    /**
     * C端用户
     */
    C("C", "C端用户"),

    /**
     * B端用户
     */
    B("B", "B端用户"),

    /**
     * 平台管理员
     */
    P("P", "平台管理员");

    private final String code;
    private final String desc;

    UserTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据code获取枚举
     */
    public static UserTypeEnum getByCode(String code) {
        for (UserTypeEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}

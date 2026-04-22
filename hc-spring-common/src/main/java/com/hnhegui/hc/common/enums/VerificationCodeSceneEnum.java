package com.hnhegui.hc.common.enums;

import lombok.Getter;

/**
 * 验证码场景枚举
 */
@Getter
public enum VerificationCodeSceneEnum {

    /**
     * 登录
     */
    LOGIN("login", "登录"),

    /**
     * 注册
     */
    REGISTER("register", "注册"),

    /**
     * 重置密码
     */
    RESET("reset", "重置密码");

    private final String code;
    private final String desc;

    VerificationCodeSceneEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据code获取枚举
     */
    public static VerificationCodeSceneEnum getByCode(String code) {
        for (VerificationCodeSceneEnum scene : values()) {
            if (scene.code.equals(code)) {
                return scene;
            }
        }
        return null;
    }
}

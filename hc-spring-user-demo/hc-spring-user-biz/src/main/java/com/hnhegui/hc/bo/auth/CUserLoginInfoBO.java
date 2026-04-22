package com.hnhegui.hc.bo.auth;

import lombok.Data;

@Data
public class CUserLoginInfoBO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 状态
     */
    private Integer status;
}

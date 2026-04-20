package com.hnhegui.hc.bo.user;

import lombok.Data;

/**
 * @author hecong
 * @since 2026/4/17 16:27
 */
@Data
public class UserCreateBO {
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 姓名
     */
    private String name;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 手机
     */
    private String phone;
    /**
     * 状态
     */
    private Integer status;
}

package com.hnhegui.hc.bo.user;

import lombok.Data;

/**
 * @author hecong
 * @since 2026/4/17 16:27
 */
@Data
public class UserCreateBO {

    private String username;
    private String password;
    private String name;
    private String email;
    private String phone;
    private Integer status;
}

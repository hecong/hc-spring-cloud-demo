package com.hnhegui.hc.bo.user;

import lombok.Data;

/**
 * @author hecong
 * @since 2026/4/17 15:22
 */
@Data
public class UserBO {

    private Long id;
    private String username;
    private String password;
    private String name;
    private String email;
    private String phone;
    private Integer status;
}

package com.hnhegui.hc.controller.user.request;

import lombok.Data;

@Data
public class UserRequest {
    private String username;
    private String password;
    private String name;
    private String email;
    private String phone;
    private Integer status;
}

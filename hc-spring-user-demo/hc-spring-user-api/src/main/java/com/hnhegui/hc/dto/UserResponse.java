package com.hnhegui.hc.dto;

import lombok.Data;
import java.util.Date;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String name;
    private String email;
    private String phone;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}

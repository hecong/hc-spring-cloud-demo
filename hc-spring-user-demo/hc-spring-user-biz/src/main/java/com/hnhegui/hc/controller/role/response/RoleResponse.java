package com.hnhegui.hc.controller.role.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RoleResponse {
    private Long id;
    private String name;
    private String code;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

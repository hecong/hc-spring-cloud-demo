package com.hnhegui.hc.controller.permission.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PermissionResponse {
    private Long id;
    private String name;
    private String code;
    private String type;
    private String path;
    private Long parentId;
    private LocalDateTime  createTime;
    private LocalDateTime updateTime;
}

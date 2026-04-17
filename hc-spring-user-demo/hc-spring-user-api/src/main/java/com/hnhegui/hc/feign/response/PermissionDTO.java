package com.hnhegui.hc.feign.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PermissionDTO {
    private Long id;
    private String name;
    private String code;
    private String type;
    private String path;
    private Long parentId;
    private LocalDateTime  createTime;
    private LocalDateTime updateTime;
}

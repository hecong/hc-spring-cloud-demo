package com.hnhegui.hc.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

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

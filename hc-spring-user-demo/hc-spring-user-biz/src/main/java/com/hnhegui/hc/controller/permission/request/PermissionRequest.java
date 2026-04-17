package com.hnhegui.hc.controller.permission.request;

import lombok.Data;

@Data
public class PermissionRequest {
    private String name;
    private String code;
    private String type;
    private String path;
    private Long parentId;
}

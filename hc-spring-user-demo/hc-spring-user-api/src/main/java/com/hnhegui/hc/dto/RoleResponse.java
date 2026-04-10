package com.hnhegui.hc.dto;

import lombok.Data;
import java.util.Date;

@Data
public class RoleResponse {
    private Long id;
    private String name;
    private String code;
    private String description;
    private Date createTime;
    private Date updateTime;
}

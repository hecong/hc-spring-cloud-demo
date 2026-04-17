package com.hnhegui.hc.feign.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author hecong
 * @since 2026/4/17 15:12
 */
@Data
public class RoleDTO {

    private Long id;
    private String name;
    private String code;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

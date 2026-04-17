package com.hnhegui.hc.feign.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author hecong
 * @since 2026/4/17 15:10
 */
@Data
public class UserDTO {
    private Long id;
    private String username;
    private String name;
    private String email;
    private String phone;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

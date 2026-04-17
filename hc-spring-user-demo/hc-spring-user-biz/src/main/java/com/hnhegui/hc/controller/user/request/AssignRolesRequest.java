package com.hnhegui.hc.controller.user.request;

import lombok.Data;

import java.util.List;

/**
 * @author hecong
 * @since 2026/4/17 09:08
 */
@Data
public class AssignRolesRequest {

    private Long userId;
    private List<Long> roleIds;
}

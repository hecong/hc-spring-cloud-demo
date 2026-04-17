package com.hnhegui.hc.controller.role.request;

import lombok.Data;

import java.util.List;

/**
 * @author hecong
 * @since 2026/4/17 09:26
 */
@Data
public class AssignPermissionsRequest {

    /**
     * 角色ID
     */
    private Long roleId;
    /**
     * 权限ID列表
     */
    private List<Long> permissionIds;
}

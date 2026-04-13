package com.hnhegui.hc.service;

import com.hnhegui.hc.dto.PermissionRequest;
import com.hnhegui.hc.dto.PermissionResponse;
import java.util.List;

public interface PermissionService {
    List<PermissionResponse> getPermissionsByRoleId(Long roleId);
    List<PermissionResponse> getPermissionsByUserId(Long userId);
    PermissionResponse savePermission(PermissionRequest permissionRequest);
    PermissionResponse updatePermission(Long id, PermissionRequest permissionRequest);
    boolean deletePermission(Long id);
    PermissionResponse getPermissionById(Long id);
    List<PermissionResponse> listPermissions();

    /**
     * 初始化动态鉴权
     */
    void initDynamicAuthRouteCache();
}
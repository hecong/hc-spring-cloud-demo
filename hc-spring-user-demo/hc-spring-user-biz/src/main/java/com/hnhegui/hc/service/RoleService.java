package com.hnhegui.hc.service;

import com.hnhegui.hc.dto.RoleRequest;
import com.hnhegui.hc.dto.RoleResponse;
import java.util.List;

public interface RoleService {
    List<RoleResponse> getRolesByUserId(Long userId);
    boolean assignPermissions(Long roleId, List<Long> permissionIds);
    RoleResponse saveRole(RoleRequest roleRequest);
    RoleResponse updateRole(Long id, RoleRequest roleRequest);
    boolean deleteRole(Long id);
    RoleResponse getRoleById(Long id);
    List<RoleResponse> listRoles();
}
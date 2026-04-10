package com.hnhegui.hc.config;

import com.hc.framework.satoken.handler.SaPermissionProvider;
import com.hnhegui.hc.dto.PermissionResponse;
import com.hnhegui.hc.dto.RoleResponse;
import com.hnhegui.hc.service.RoleService;
import com.hnhegui.hc.service.PermissionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SaPermissionProviderImpl implements SaPermissionProvider {
    private final RoleService roleService;
    private final PermissionService permissionService;

    public SaPermissionProviderImpl(RoleService roleService, PermissionService permissionService) {
        this.roleService = roleService;
        this.permissionService = permissionService;
    }

    @Override
    public List<String> getRoles(Long userId) {
        return roleService.getRolesByUserId(userId).stream()
                .map(RoleResponse::getCode)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getPermissions(Long userId) {
        return permissionService.getPermissionsByUserId(userId).stream()
                .map(PermissionResponse::getCode)
                .collect(Collectors.toList());
    }
}
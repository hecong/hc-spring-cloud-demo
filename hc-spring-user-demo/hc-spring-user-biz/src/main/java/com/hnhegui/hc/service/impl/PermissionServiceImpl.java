package com.hnhegui.hc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hnhegui.hc.converter.PermissionConverter;
import com.hnhegui.hc.dto.PermissionRequest;
import com.hnhegui.hc.dto.PermissionResponse;
import com.hnhegui.hc.entity.Permission;
import com.hnhegui.hc.entity.RolePermission;
import com.hnhegui.hc.entity.UserRole;
import com.hnhegui.hc.mapper.PermissionMapper;
import com.hnhegui.hc.mapper.RolePermissionMapper;
import com.hnhegui.hc.mapper.UserRoleMapper;
import com.hnhegui.hc.service.PermissionService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl implements PermissionService {
    private final PermissionMapper permissionMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final UserRoleMapper userRoleMapper;

    public PermissionServiceImpl(PermissionMapper permissionMapper, RolePermissionMapper rolePermissionMapper, UserRoleMapper userRoleMapper) {
        this.permissionMapper = permissionMapper;
        this.rolePermissionMapper = rolePermissionMapper;
        this.userRoleMapper = userRoleMapper;
    }

    @Override
    public List<PermissionResponse> getPermissionsByRoleId(Long roleId) {
        List<Long> permissionIds = rolePermissionMapper.selectList(new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId, roleId))
                .stream().map(RolePermission::getPermissionId).toList();
        if (permissionIds.isEmpty()) {
            return List.of();
        }
        List<Permission> permissions = permissionMapper.selectBatchIds(permissionIds);
        return permissions.stream().map(PermissionConverter.INSTANCE::toResponse).toList();
    }

    @Override
    public List<PermissionResponse> getPermissionsByUserId(Long userId) {
        List<Long> roleIds = userRoleMapper.selectList(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId))
                .stream().map(UserRole::getRoleId).toList();
        if (roleIds.isEmpty()) {
            return List.of();
        }
        List<Long> permissionIds = rolePermissionMapper.selectList(new LambdaQueryWrapper<RolePermission>().in(RolePermission::getRoleId, roleIds))
                .stream().map(RolePermission::getPermissionId).collect(Collectors.toSet()).stream().toList();
        if (permissionIds.isEmpty()) {
            return List.of();
        }
        List<Permission> permissions = permissionMapper.selectBatchIds(permissionIds);
        return permissions.stream().map(PermissionConverter.INSTANCE::toResponse).toList();
    }

    @Override
    public PermissionResponse savePermission(PermissionRequest permissionRequest) {
        Permission permission = PermissionConverter.INSTANCE.toEntity(permissionRequest);
        permissionMapper.insert(permission);
        return PermissionConverter.INSTANCE.toResponse(permission);
    }

    @Override
    public PermissionResponse updatePermission(Long id, PermissionRequest permissionRequest) {
        Permission permission = PermissionConverter.INSTANCE.toEntity(permissionRequest);
        permission.setId(id);
        permissionMapper.updateById(permission);
        return PermissionConverter.INSTANCE.toResponse(permission);
    }

    @Override
    public boolean deletePermission(Long id) {
        return permissionMapper.deleteById(id) > 0;
    }

    @Override
    public PermissionResponse getPermissionById(Long id) {
        Permission permission = permissionMapper.selectById(id);
        return PermissionConverter.INSTANCE.toResponse(permission);
    }

    @Override
    public List<PermissionResponse> listPermissions() {
        List<Permission> permissions = permissionMapper.selectList(null);
        return permissions.stream().map(PermissionConverter.INSTANCE::toResponse).toList();
    }
}
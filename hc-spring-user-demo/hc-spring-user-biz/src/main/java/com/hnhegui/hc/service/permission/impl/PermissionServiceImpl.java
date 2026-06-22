package com.hnhegui.hc.service.permission.impl;

import com.hc.framework.mybatis.service.BaseServiceImpl;
import com.hnhegui.hc.controller.permission.converter.PermissionConverter;
import com.hnhegui.hc.entity.permission.Permission;
import com.hnhegui.hc.entity.role.RolePermission;
import com.hnhegui.hc.controller.permission.request.PermissionRequest;
import com.hnhegui.hc.controller.permission.response.PermissionResponse;
import com.hnhegui.hc.mapper.permission.PermissionMapper;
import com.hnhegui.hc.mapper.role.RolePermissionMapper;
import com.hnhegui.hc.mapper.user.UserRoleMapper;
import com.hnhegui.hc.service.auth.PermissionCacheRefreshService;
import com.hnhegui.hc.service.permission.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionServiceImpl extends BaseServiceImpl<PermissionMapper, Permission> implements PermissionService {
    private final PermissionMapper permissionMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final UserRoleMapper userRoleMapper;
    private final TransactionTemplate transactionTemplate;
    private final PermissionCacheRefreshService permissionCacheRefreshService;

    @Override
    public List<PermissionResponse> getPermissionsByRoleId(Long roleId) {
        List<Long> permissionIds = rolePermissionMapper.selectPermissionIdsByRoleId(roleId);
        if (permissionIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<Permission> permissions = permissionMapper.selectByIds(permissionIds);
        return PermissionConverter.INSTANCE.toResponseList(permissions);
    }

    @Override
    public List<PermissionResponse> getPermissionsByUserId(Long userId) {
        List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);
        if (roleIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> permissionIds = new HashSet<>(rolePermissionMapper.selectPermissionIdsByRoleIds(roleIds)).stream().toList();
        if (permissionIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<Permission> permissions = permissionMapper.selectByIds(permissionIds);
        return PermissionConverter.INSTANCE.toResponseList(permissions);
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
        // 查找哪些角色拥有该权限
        List<Long> affectedRoleIds = rolePermissionMapper.selectList(
                com.baomidou.mybatisplus.core.toolkit.Wrappers.<RolePermission>lambdaQuery()
                    .select(RolePermission::getRoleId)
                    .eq(RolePermission::getPermissionId, id))
            .stream().map(RolePermission::getRoleId).toList();

        // 查找这些角色下的所有用户
        java.util.Set<Long> affectedUserIds = new java.util.LinkedHashSet<>();
        for (Long roleId : affectedRoleIds) {
            affectedUserIds.addAll(userRoleMapper.selectUserIdsByRoleId(roleId));
        }

        boolean result = Boolean.TRUE.equals(transactionTemplate.execute(status -> {
            rolePermissionMapper.deleteByPermissionId(id);
            return permissionMapper.deleteById(id) > 0;
        }));

        // 刷新受影响用户的权限缓存
        if (result && !affectedUserIds.isEmpty()) {
            permissionCacheRefreshService.refreshUsers(affectedUserIds.stream().toList());
        }

        return result;
    }

    @Override
    public PermissionResponse getPermissionById(Long id) {
        Permission permission = permissionMapper.selectById(id);
        return PermissionConverter.INSTANCE.toResponse(permission);
    }

    @Override
    public List<PermissionResponse> listPermissions() {
        List<Permission> permissions = permissionMapper.selectList(null);
        return PermissionConverter.INSTANCE.toResponseList(permissions);
    }

    @Override
    public int insertBatch(List<Permission> list) {
        return permissionMapper.insertBatch(list);
    }

    @Override
    public int insertOrUpdateBatch(List<Permission> list) {
        return permissionMapper.insertOrUpdateBatch(list);
    }
}
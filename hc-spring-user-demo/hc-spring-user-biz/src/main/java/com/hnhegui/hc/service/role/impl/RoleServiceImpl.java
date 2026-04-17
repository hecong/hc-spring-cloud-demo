package com.hnhegui.hc.service.role.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hc.framework.mybatis.service.BaseServiceImpl;
import com.hnhegui.hc.controller.role.converter.RoleConverter;
import com.hnhegui.hc.entity.role.Role;
import com.hnhegui.hc.entity.role.RolePermission;
import com.hnhegui.hc.entity.user.UserRole;
import com.hnhegui.hc.controller.role.request.RoleRequest;
import com.hnhegui.hc.controller.role.response.RoleResponse;
import com.hnhegui.hc.mapper.role.RoleMapper;
import com.hnhegui.hc.mapper.role.RolePermissionMapper;
import com.hnhegui.hc.mapper.user.UserRoleMapper;
import com.hnhegui.hc.service.role.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends BaseServiceImpl<RoleMapper, Role> implements RoleService {
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final TransactionTemplate transactionTemplate;


    @Override
    public List<RoleResponse> getRolesByUserId(Long userId) {
        List<Long> roleIds = userRoleMapper.selectList(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId))
            .stream().map(UserRole::getRoleId).toList();
        if (roleIds.isEmpty()) {
            return List.of();
        }
        List<Role> roles = roleMapper.selectBatchIds(roleIds);
        return RoleConverter.INSTANCE.toResponseList(roles);
    }

    @Override
    public boolean assignPermissions(Long roleId, List<Long> permissionIds) {
        // 构建新的角色权限关联列表
        List<RolePermission> newRolePermissions = permissionIds.stream()
            .map(permissionId -> {
                RolePermission rolePermission = new RolePermission();
                rolePermission.setRoleId(roleId);
                rolePermission.setPermissionId(permissionId);
                return rolePermission;
            })
            .toList();

        return Boolean.TRUE.equals(transactionTemplate.execute(status -> {
            // 物理删除该角色的所有权限关联
            rolePermissionMapper.deletePhysicalByRoleId(roleId);

            // 如果没有新权限需要分配，直接返回成功
            if (newRolePermissions.isEmpty()) {
                return true;
            }

            // 批量插入新的权限关联
            return rolePermissionMapper.insertBatch(newRolePermissions) > 0;
        }));
    }

    @Override
    public RoleResponse saveRole(RoleRequest roleRequest) {
        Role role = RoleConverter.INSTANCE.toEntity(roleRequest);
        roleMapper.insert(role);
        return RoleConverter.INSTANCE.toResponse(role);
    }

    @Override
    public RoleResponse updateRole(Long id, RoleRequest roleRequest) {
        Role role = RoleConverter.INSTANCE.toEntity(roleRequest);
        role.setId(id);
        roleMapper.updateById(role);
        return RoleConverter.INSTANCE.toResponse(role);
    }

    @Override
    public boolean deleteRole(Long id) {
        return roleMapper.deleteById(id) > 0;
    }

    @Override
    public RoleResponse getRoleById(Long id) {
        Role role = roleMapper.selectById(id);
        return RoleConverter.INSTANCE.toResponse(role);
    }

    @Override
    public List<RoleResponse> listRoles() {
        List<Role> roles = roleMapper.selectList(null);
        return RoleConverter.INSTANCE.toResponseList(roles);
    }

    @Override
    public int insertBatch(List<Role> list) {
        return roleMapper.insertBatch(list);
    }

    @Override
    public int insertOrUpdateBatch(List<Role> list) {
        return roleMapper.insertOrUpdateBatch(list);
    }
}
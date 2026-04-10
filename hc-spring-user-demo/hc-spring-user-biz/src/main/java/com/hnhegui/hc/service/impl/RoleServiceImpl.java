package com.hnhegui.hc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hnhegui.hc.converter.RoleConverter;
import com.hnhegui.hc.dto.RoleRequest;
import com.hnhegui.hc.dto.RoleResponse;
import com.hnhegui.hc.entity.Role;
import com.hnhegui.hc.entity.RolePermission;
import com.hnhegui.hc.entity.UserRole;
import com.hnhegui.hc.mapper.RoleMapper;
import com.hnhegui.hc.mapper.RolePermissionMapper;
import com.hnhegui.hc.mapper.UserRoleMapper;
import com.hnhegui.hc.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final PlatformTransactionManager transactionManager;

    public RoleServiceImpl(RoleMapper roleMapper, UserRoleMapper userRoleMapper, RolePermissionMapper rolePermissionMapper, PlatformTransactionManager transactionManager) {
        this.roleMapper = roleMapper;
        this.userRoleMapper = userRoleMapper;
        this.rolePermissionMapper = rolePermissionMapper;
        this.transactionManager = transactionManager;
    }

    @Override
    public List<RoleResponse> getRolesByUserId(Long userId) {
        List<Long> roleIds = userRoleMapper.selectList(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId))
                .stream().map(UserRole::getRoleId).toList();
        if (roleIds.isEmpty()) {
            return List.of();
        }
        List<Role> roles = roleMapper.selectBatchIds(roleIds);
        return roles.stream().map(RoleConverter.INSTANCE::toResponse).toList();
    }

    @Override
    public boolean assignPermissions(Long roleId, List<Long> permissionIds) {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            rolePermissionMapper.delete(new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId, roleId));
            for (Long permissionId : permissionIds) {
                RolePermission rolePermission = new RolePermission();
                rolePermission.setRoleId(roleId);
                rolePermission.setPermissionId(permissionId);
                rolePermissionMapper.insert(rolePermission);
            }
            transactionManager.commit(status);
            return true;
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw e;
        }
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
        return roles.stream().map(RoleConverter.INSTANCE::toResponse).toList();
    }
}
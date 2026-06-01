package com.hnhegui.hc.mapper.role;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hnhegui.hc.entity.role.RolePermission;

import java.util.List;

public interface RolePermissionMapper extends BaseMapper<RolePermission> {

    /**
     * 批量插入
     * @param newRolePermissions 新的角色权限列表
     * @return 影响行数
     */
    int insertBatch(List<RolePermission> newRolePermissions);

    /**
     * 根据角色ID物理删除权限关联
     * @param roleId 角色ID
     * @return 影响行数
     */
    int deletePhysicalByRoleId(Long roleId);

    /**
     * 根据角色ID查询权限ID列表
     *
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    default List<Long> selectPermissionIdsByRoleId(Long roleId) {
        return this.selectList(Wrappers.<RolePermission>lambdaQuery()
                .select(RolePermission::getPermissionId)
                .eq(RolePermission::getRoleId, roleId))
            .stream().map(RolePermission::getPermissionId).toList();
    }

    /**
     * 根据角色ID列表查询权限ID列表
     *
     * @param roleIds 角色ID列表
     * @return 权限ID列表
     */
    default List<Long> selectPermissionIdsByRoleIds(List<Long> roleIds) {
        return this.selectList(Wrappers.<RolePermission>lambdaQuery()
                .select(RolePermission::getPermissionId)
                .in(RolePermission::getRoleId, roleIds))
            .stream().map(RolePermission::getPermissionId).toList();
    }
}
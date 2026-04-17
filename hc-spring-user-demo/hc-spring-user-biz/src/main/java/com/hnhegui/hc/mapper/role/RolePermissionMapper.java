package com.hnhegui.hc.mapper.role;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
}
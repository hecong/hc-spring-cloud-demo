package com.hnhegui.hc.mapper.role;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hnhegui.hc.entity.role.SysRolePermDataScope;

import java.util.List;

/**
 * 角色-权限数据范围 Mapper
 *
 * @author hecong
 * @since 2026/6/11
 */
public interface SysRolePermDataScopeMapper extends BaseMapper<SysRolePermDataScope> {

    /**
     * 根据角色ID列表和权限ID查询数据范围
     */
    default List<SysRolePermDataScope> selectByRoleIdsAndPermissionId(
            List<Long> roleIds, Long permissionId) {
        return this.selectList(Wrappers.<SysRolePermDataScope>lambdaQuery()
            .in(SysRolePermDataScope::getRoleId, roleIds)
            .eq(SysRolePermDataScope::getPermissionId, permissionId));
    }
}

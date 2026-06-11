package com.hnhegui.hc.mapper.role;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hnhegui.hc.entity.role.SysRolePermDeptScope;

import java.util.List;

/**
 * 角色-权限自定义部门范围明细 Mapper
 *
 * @author hecong
 * @since 2026/6/11
 */
public interface SysRolePermDeptScopeMapper extends BaseMapper<SysRolePermDeptScope> {

    /**
     * 根据 scopeId 列表查询自定义部门ID列表
     */
    default List<Long> selectDeptIdsByScopeIds(List<Long> scopeIds) {
        return this.selectList(Wrappers.<SysRolePermDeptScope>lambdaQuery()
                .select(SysRolePermDeptScope::getDeptId)
                .in(SysRolePermDeptScope::getScopeId, scopeIds))
            .stream()
            .map(SysRolePermDeptScope::getDeptId)
            .toList();
    }
}

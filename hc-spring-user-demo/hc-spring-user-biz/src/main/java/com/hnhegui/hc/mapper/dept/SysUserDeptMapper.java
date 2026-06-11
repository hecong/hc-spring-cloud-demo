package com.hnhegui.hc.mapper.dept;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hnhegui.hc.entity.dept.SysUserDept;

import java.util.List;

/**
 * 用户-部门关联 Mapper
 *
 * @author hecong
 * @since 2026/6/11
 */
public interface SysUserDeptMapper extends BaseMapper<SysUserDept> {

    /**
     * 根据用户ID查询部门ID列表
     */
    default List<Long> selectDeptIdsByUserId(Long userId) {
        return this.selectList(Wrappers.<SysUserDept>lambdaQuery()
                .select(SysUserDept::getDeptId)
                .eq(SysUserDept::getUserId, userId))
            .stream()
            .map(SysUserDept::getDeptId)
            .toList();
    }
}

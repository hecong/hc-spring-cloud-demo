package com.hnhegui.hc.mapper.dept;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hnhegui.hc.entity.dept.SysDept;

import java.util.List;

/**
 * 部门 Mapper
 *
 * @author hecong
 * @since 2026/6/11
 */
public interface SysDeptMapper extends BaseMapper<SysDept> {

    /**
     * 根据祖先ID查询所有子部门ID
     *
     * <p>用于 DEPT_AND_CHILDREN 数据权限的部门树展开。
     * 通过 FIND_IN_SET 匹配 ancestors 字段，返回该部门下的所有子部门ID。</p>
     *
     * @param ancestorId 祖先部门ID
     * @return 所有子部门ID列表
     */
    default List<Long> selectChildIdsByAncestor(Long ancestorId) {
        String ancestorStr = String.valueOf(ancestorId);
        return this.selectList(Wrappers.<SysDept>lambdaQuery()
                .select(SysDept::getId)
                .eq(SysDept::getDeleted, 0)
                .apply("FIND_IN_SET({0}, ancestors) > 0", ancestorStr))
            .stream()
            .map(SysDept::getId)
            .toList();
    }
}

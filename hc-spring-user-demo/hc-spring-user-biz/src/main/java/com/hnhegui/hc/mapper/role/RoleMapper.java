package com.hnhegui.hc.mapper.role;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hnhegui.hc.entity.role.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoleMapper extends BaseMapper<Role> {
    /**
     * 插入或更新批量
     */
    int insertOrUpdateBatch(@Param("list") List<Role> list);

    /**
     * 插入批量
     */
    int insertBatch(@Param("list") List<Role> list);
}
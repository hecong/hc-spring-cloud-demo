package com.hnhegui.hc.mapper.permission;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hnhegui.hc.entity.permission.Permission;

import java.util.List;

public interface PermissionMapper extends BaseMapper<Permission> {
    /**
     * 插入数据
     *
     * @param list 数据列表
     * @return int 影响行数
     */
    int insertBatch(List<Permission> list);

    /**
     * 插入或更新数据
     *
     * @param list 数据列表
     * @return int 影响行数
     */
    int insertOrUpdateBatch(List<Permission> list);
}
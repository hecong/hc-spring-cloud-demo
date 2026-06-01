package com.hnhegui.hc.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hnhegui.hc.entity.user.UserRole;

import java.util.List;

public interface UserRoleMapper extends BaseMapper<UserRole> {

    /**
     * 根据用户ID查询关联记录
     *
     * @param userId 用户ID
     * @return 用户角色关联列表
     */
    default List<UserRole> selectByUserId(Long userId) {
        return this.selectList(Wrappers.<UserRole>lambdaQuery()
            .eq(UserRole::getUserId, userId));
    }

    /**
     * 根据用户ID删除所有角色关联
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    default int deleteByUserId(Long userId) {
        return this.delete(Wrappers.<UserRole>lambdaQuery()
            .eq(UserRole::getUserId, userId));
    }

    /**
     * 根据角色ID查询用户ID列表
     *
     * @param roleId 角色ID
     * @return 用户ID列表
     */
    default List<Long> selectUserIdsByRoleId(Long roleId) {
        return this.selectList(Wrappers.<UserRole>lambdaQuery()
                .select(UserRole::getUserId)
                .eq(UserRole::getRoleId, roleId))
            .stream().map(UserRole::getUserId).toList();
    }

    /**
     * 根据用户ID查询角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    default List<Long> selectRoleIdsByUserId(Long userId) {
        return this.selectList(Wrappers.<UserRole>lambdaQuery()
                .select(UserRole::getRoleId)
                .eq(UserRole::getUserId, userId))
            .stream().map(UserRole::getRoleId).toList();
    }
}
package com.hnhegui.hc.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnhegui.hc.entity.user.UserRole;
import com.hnhegui.hc.mapper.user.UserRoleMapper;
import com.hnhegui.hc.service.user.UserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

/**
 * @author hecong
 * @since 2026/4/17 09:05
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

    private final TransactionTemplate transactionTemplate;


    // ====================== 分配角色 ======================
    @Override
    @Caching(evict = {
        @CacheEvict(value = "user#1h", key = "#userId"),
        @CacheEvict(value = "user#10m", key = "'list'")
    })
    public boolean assignRoles(Long userId, List<Long> roleIds) {
        // 批量插入新角色
        List<UserRole> userRoles = roleIds.stream().map(roleId -> {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            return userRole;
        }).toList();
        transactionTemplate.execute(status -> {
            // 先删除旧角色
            this.baseMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));
            // 批量插入新角色
            return saveBatch(userRoles);
        });
        return !userRoles.isEmpty();
    }
}

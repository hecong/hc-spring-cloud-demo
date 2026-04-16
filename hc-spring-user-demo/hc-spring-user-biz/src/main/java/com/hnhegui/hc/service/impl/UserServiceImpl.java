package com.hnhegui.hc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hnhegui.hc.converter.UserConverter;
import com.hnhegui.hc.dto.UserRequest;
import com.hnhegui.hc.dto.UserResponse;
import com.hnhegui.hc.entity.User;
import com.hnhegui.hc.entity.UserRole;
import com.hnhegui.hc.mapper.UserMapper;
import com.hnhegui.hc.mapper.UserRoleMapper;
import com.hnhegui.hc.service.UserService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;

    public UserServiceImpl(UserMapper userMapper, UserRoleMapper userRoleMapper) {
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
    }

    // ====================== 查询 ======================
    @Override
    @Cacheable(value = "user#1h", key = "#username")
    public UserResponse getUserByUsername(String username) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        return UserConverter.INSTANCE.toResponse(user);
    }

    @Override
    @Cacheable(value = "user#1h", key = "#id")
    public UserResponse getUserById(Long id) {
        User user = userMapper.selectById(id);
        return UserConverter.INSTANCE.toResponse(user);
    }

    @Override
    @Cacheable(value = "user#10m", key = "'list'")
    public List<UserResponse> listUsers() {
        List<User> users = userMapper.selectList(null);
        return UserConverter.INSTANCE.toResponseList(users);
    }

    @Override
    public List<UserResponse> getUsersByRoleId(Long roleId) {
        List<Long> userIds = userRoleMapper.selectList(new LambdaQueryWrapper<UserRole>().eq(UserRole::getRoleId, roleId))
            .stream().map(UserRole::getUserId).toList();
        if (userIds.isEmpty()) {
            return List.of();
        }
        List<User> users = userMapper.selectBatchIds(userIds);
        return UserConverter.INSTANCE.toResponseList(users);
    }

    // ====================== 保存 ======================
    @Override
    @Cacheable(value = "user#1h", key = "#result.id")
    public UserResponse saveUser(UserRequest userRequest) {
        User user = UserConverter.INSTANCE.toEntity(userRequest);
        userMapper.insert(user);
        return UserConverter.INSTANCE.toResponse(user);
    }

    // ====================== 更新 ======================
    @Override
    @Transactional
    @Caching(
        put = @CachePut(value = "user#1h", key = "#id"),
        evict = {
            @CacheEvict(value = "user#1h", key = "#userRequest.username"),
            @CacheEvict(value = "user#10m", key = "'list'")
        }
    )
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        User user = UserConverter.INSTANCE.toEntity(userRequest);
        user.setId(id);
        userMapper.updateById(user);
        return UserConverter.INSTANCE.toResponse(user);
    }

    // ====================== 删除 ======================
    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "user#1h", key = "#id"),
        @CacheEvict(value = "user#10m", key = "'list'")
    })
    public boolean deleteUser(Long id) {
        // 同时删除用户角色，保证数据一致
        userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, id));
        return userMapper.deleteById(id) > 0;
    }

    // ====================== 分配角色 ======================
    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "user#1h", key = "#userId"),
        @CacheEvict(value = "user#10m", key = "'list'")
    })
    public boolean assignRoles(Long userId, List<Long> roleIds) {
        // 先删除旧角色
        userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));
        // 批量插入新角色
        for (Long roleId : roleIds) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoleMapper.insert(userRole);
        }
        return true;
    }
}
package com.hnhegui.hc.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hc.framework.mybatis.service.BaseServiceImpl;
import com.hnhegui.hc.bo.user.UserBO;
import com.hnhegui.hc.bo.user.UserCreateBO;
import com.hnhegui.hc.bo.user.UserPageQueryBO;
import com.hnhegui.hc.controller.user.converter.UserConverter;
import com.hnhegui.hc.entity.user.User;
import com.hnhegui.hc.entity.user.UserRole;
import com.hnhegui.hc.mapper.user.UserMapper;
import com.hnhegui.hc.mapper.user.UserRoleMapper;
import com.hnhegui.hc.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends BaseServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final TransactionTemplate transactionTemplate;


    // ====================== 查询 ======================
    @Override
    public UserBO getUserByUsername(String username) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        return UserConverter.INSTANCE.entityToBo(user);
    }

    @Override
    @Cacheable(value = "user#1h", key = "#id")
    public UserBO getUserById(Long id) {
        User user = userMapper.selectById(id);
        return UserConverter.INSTANCE.entityToBo(user);
    }

    @Override
    @Cacheable(value = "user#10m", key = "'list'")
    public List<UserBO> listUsers() {
        List<User> users = userMapper.selectList(null);
        return UserConverter.INSTANCE.entityToBoList(users);
    }

    @Override
    public Page<UserBO> listUsersByPage(UserPageQueryBO userPageQueryBO) {
        return UserConverter.INSTANCE.entityPageToBoPage(userMapper.selectUsersByPage(userPageQueryBO));
    }

    @Override
    public List<UserBO> getUsersByRoleId(Long roleId) {
        List<Long> userIds = userRoleMapper.selectList(new LambdaQueryWrapper<UserRole>().eq(UserRole::getRoleId, roleId))
            .stream().map(UserRole::getUserId).toList();
        if (userIds.isEmpty()) {
            return List.of();
        }
        List<User> users = userMapper.selectBatchIds(userIds);
        return UserConverter.INSTANCE.entityToBoList(users);
    }

    // ====================== 保存 ======================
    @Override
    @CachePut(value = "user#1h", key = "#result.id")
    public UserBO saveUser(UserCreateBO userCreateBO) {
        User user = UserConverter.INSTANCE.createBoToEntity(userCreateBO);
        userMapper.insert(user);
        return UserConverter.INSTANCE.entityToBo(user);
    }

    // ====================== 更新 ======================
    @Override
    @Caching(
        put = @CachePut(value = "user#1h", key = "#id"),
        evict = {
            @CacheEvict(value = "user#10m", key = "'list'")
        }
    )
    public UserBO updateUser(Long id, UserCreateBO userCreateBO) {
        User user = UserConverter.INSTANCE.createBoToEntity(userCreateBO);
        user.setId(id);
        userMapper.updateById(user);
        return UserConverter.INSTANCE.entityToBo(user);
    }

    // ====================== 删除 ======================
    @Override
    @Caching(evict = {
        @CacheEvict(value = "user#1h", key = "#id"),
        @CacheEvict(value = "user#10m", key = "'list'")
    })
    public boolean deleteUser(Long id) {
        // 同时删除用户角色，保证数据一致
        return Boolean.TRUE.equals(transactionTemplate.execute(status -> {
            int delete = userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, id));
            if (delete > 0) {
                int i = userMapper.deleteById(id);
                return i > 0;
            }
            return false;
        }));
    }


    @Override
    public int insertBatch(List<User> list) {
        return userMapper.insertBatch(list);
    }

    @Override
    public int insertOrUpdateBatch(List<User> list) {
        return userMapper.insertOrUpdateBatch(list);
    }
}
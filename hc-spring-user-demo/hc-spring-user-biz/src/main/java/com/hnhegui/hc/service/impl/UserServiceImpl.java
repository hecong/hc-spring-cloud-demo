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
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final PlatformTransactionManager transactionManager;

    public UserServiceImpl(UserMapper userMapper, UserRoleMapper userRoleMapper, PlatformTransactionManager transactionManager) {
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
        this.transactionManager = transactionManager;
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        return UserConverter.INSTANCE.toResponse(user);
    }

    @Override
    public List<UserResponse> getUsersByRoleId(Long roleId) {
        List<Long> userIds = userRoleMapper.selectList(new LambdaQueryWrapper<UserRole>().eq(UserRole::getRoleId, roleId))
                .stream().map(UserRole::getUserId).toList();
        if (userIds.isEmpty()) {
            return List.of();
        }
        List<User> users = userMapper.selectBatchIds(userIds);
        return users.stream().map(UserConverter.INSTANCE::toResponse).toList();
    }

    @Override
    public boolean assignRoles(Long userId, List<Long> roleIds) {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));
            for (Long roleId : roleIds) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRoleMapper.insert(userRole);
            }
            transactionManager.commit(status);
            return true;
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw e;
        }
    }

    @Override
    public UserResponse saveUser(UserRequest userRequest) {
        User user = UserConverter.INSTANCE.toEntity(userRequest);
        userMapper.insert(user);
        return UserConverter.INSTANCE.toResponse(user);
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        User user = UserConverter.INSTANCE.toEntity(userRequest);
        user.setId(id);
        userMapper.updateById(user);
        return UserConverter.INSTANCE.toResponse(user);
    }

    @Override
    public boolean deleteUser(Long id) {
        return userMapper.deleteById(id) > 0;
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userMapper.selectById(id);
        return UserConverter.INSTANCE.toResponse(user);
    }

    @Override
    public List<UserResponse> listUsers() {
        List<User> users = userMapper.selectList(null);
        return users.stream().map(UserConverter.INSTANCE::toResponse).toList();
    }
}
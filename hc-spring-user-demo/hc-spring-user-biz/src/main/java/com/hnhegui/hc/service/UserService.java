package com.hnhegui.hc.service;

import com.hnhegui.hc.dto.UserRequest;
import com.hnhegui.hc.dto.UserResponse;
import com.hnhegui.hc.entity.User;
import java.util.List;

public interface UserService {
    UserResponse getUserByUsername(String username);
    List<UserResponse> getUsersByRoleId(Long roleId);
    boolean assignRoles(Long userId, List<Long> roleIds);
    UserResponse saveUser(UserRequest userRequest);
    UserResponse updateUser(Long id, UserRequest userRequest);
    boolean deleteUser(Long id);
    UserResponse getUserById(Long id);
    List<UserResponse> listUsers();
}
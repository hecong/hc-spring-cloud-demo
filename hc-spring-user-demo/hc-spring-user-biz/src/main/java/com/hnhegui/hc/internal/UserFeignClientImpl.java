package com.hnhegui.hc.internal;

import com.hnhegui.hc.dto.PermissionResponse;
import com.hnhegui.hc.dto.RoleResponse;
import com.hnhegui.hc.dto.UserResponse;
import com.hnhegui.hc.feign.UserFeignClient;
import com.hnhegui.hc.service.PermissionService;
import com.hnhegui.hc.service.RoleService;
import com.hnhegui.hc.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户服务内部接口实现 - 供 Feign 客户端调用
 * 实现 UserFeignClient 接口，返回原始数据，不使用 Result 包装
 */
@RestController
@RequiredArgsConstructor
public class UserFeignClientImpl implements UserFeignClient {

    private final UserService userService;
    private final RoleService roleService;
    private final PermissionService permissionService;


    @Override
    public List<UserResponse> listUsers() {
        return userService.listUsers();
    }

    @Override
    public UserResponse getUserById(@PathVariable("id") Long id) {
        return userService.getUserById(id);
    }

    @Override
    public UserResponse getUserByUsername(@PathVariable("username") String username) {
        return userService.getUserByUsername(username);
    }

    @Override
    public List<RoleResponse> listRoles() {
        return roleService.listRoles();
    }

    @Override
    public RoleResponse getRoleById(@PathVariable("id") Long id) {
        return roleService.getRoleById(id);
    }

    @Override
    public List<PermissionResponse> listPermissions() {
        return permissionService.listPermissions();
    }

    @Override
    public PermissionResponse getPermissionById(@PathVariable("id") Long id) {
        return permissionService.getPermissionById(id);
    }

    @Override
    public List<RoleResponse> getRolesByUserId(@PathVariable("userId") Long userId) {
        return roleService.getRolesByUserId(userId);
    }

    @Override
    public List<PermissionResponse> getPermissionsByUserId(@PathVariable("userId") Long userId) {
        return permissionService.getPermissionsByUserId(userId);
    }
}

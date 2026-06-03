package com.hnhegui.hc.internal.user;

import com.hnhegui.hc.controller.permission.response.PermissionResponse;
import com.hnhegui.hc.controller.role.response.RoleResponse;
import com.hnhegui.hc.feign.response.UserDTO;
import com.hnhegui.hc.feign.UserFeignClient;
import com.hnhegui.hc.internal.user.converter.UserDTOConverter;
import com.hnhegui.hc.service.permission.PermissionService;
import com.hnhegui.hc.service.role.RoleService;
import com.hnhegui.hc.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

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
    public List<UserDTO> listUsers() {
        return UserDTOConverter.INSTANCE.convertToUserDTOList(userService.listUsers());
    }

    @Override
    public UserDTO getUserById(@PathVariable("id") Long id) {
        return UserDTOConverter.INSTANCE.convertToUserDTO(userService.getUserById(id));
    }

    @Override
    public UserDTO getUserByUsername(@PathVariable("username") String username) {
        return UserDTOConverter.INSTANCE.convertToUserDTO(userService.getUserByUsername(username));
    }

    @Override
    public List<String> getRoleCodesByUserId(@PathVariable("userId") Long userId) {
        return roleService.getRolesByUserId(userId).stream()
                .map(RoleResponse::getCode)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getPermissionCodesByUserId(@PathVariable("userId") Long userId) {
        return permissionService.getPermissionsByUserId(userId).stream()
                .map(PermissionResponse::getCode)
                .collect(Collectors.toList());
    }
}

package com.hnhegui.hc.feign;

import com.hnhegui.hc.dto.PermissionResponse;
import com.hnhegui.hc.dto.RoleResponse;
import com.hnhegui.hc.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 用户服务 Feign 客户端 - 供内部服务调用
 * 返回原始数据，不使用 Result 包装
 */
@FeignClient(name = "user-service", fallbackFactory = UserFeignClientFallbackFactory.class)
public interface UserFeignClient {

    @GetMapping("/api/internal/user/list")
    List<UserResponse> listUsers();

    @GetMapping("/api/internal/user/get/{id}")
    UserResponse getUserById(@PathVariable("id") Long id);

    @GetMapping("/api/internal/user/getByUsername/{username}")
    UserResponse getUserByUsername(@PathVariable("username") String username);

    @GetMapping("/api/internal/role/list")
    List<RoleResponse> listRoles();

    @GetMapping("/api/internal/role/get/{id}")
    RoleResponse getRoleById(@PathVariable("id") Long id);

    @GetMapping("/api/internal/permission/list")
    List<PermissionResponse> listPermissions();

    @GetMapping("/api/internal/permission/get/{id}")
    PermissionResponse getPermissionById(@PathVariable("id") Long id);

    @GetMapping("/api/internal/role/getByUserId/{userId}")
    List<RoleResponse> getRolesByUserId(@PathVariable("userId") Long userId);

    @GetMapping("/api/internal/permission/getByUserId/{userId}")
    List<PermissionResponse> getPermissionsByUserId(@PathVariable("userId") Long userId);
}

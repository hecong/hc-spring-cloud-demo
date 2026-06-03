package com.hnhegui.hc.feign;

import com.hnhegui.hc.feign.response.UserDTO;
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

    /**
     * 列出所有用户
     *
     * @return 用户列表
     */
    @GetMapping("/api/internal/user/list")
    List<UserDTO> listUsers();

    /**
     * 根据ID获取用户
     *
     * @param id 用户ID
     * @return 用户
     */
    @GetMapping("/api/internal/user/get/{id}")
    UserDTO getUserById(@PathVariable("id") Long id);

    /**
     * 根据用户名获取用户
     *
     * @param username 用户名
     * @return 用户
     */

    @GetMapping("/api/internal/user/getByUsername/{username}")
    UserDTO getUserByUsername(@PathVariable("username") String username);

    /**
     * 根据用户ID获取角色编码列表
     *
     * @param userId 用户ID
     * @return 角色编码列表
     */
    @GetMapping("/api/internal/user/roles/{userId}")
    List<String> getRoleCodesByUserId(@PathVariable("userId") Long userId);

    /**
     * 根据用户ID获取权限编码列表
     *
     * @param userId 用户ID
     * @return 权限编码列表
     */
    @GetMapping("/api/internal/user/permissions/{userId}")
    List<String> getPermissionCodesByUserId(@PathVariable("userId") Long userId);

}

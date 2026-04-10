package com.hnhegui.hc.feign;

import com.hnhegui.hc.dto.PermissionResponse;
import com.hnhegui.hc.dto.RoleResponse;
import com.hnhegui.hc.dto.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * UserFeignClient 降级工厂
 */
@Slf4j
@Component
public class UserFeignClientFallbackFactory implements FallbackFactory<UserFeignClient> {

    @Override
    public UserFeignClient create(Throwable cause) {
        log.error("UserFeignClient 调用失败: {}", cause.getMessage());
        return new UserFeignClient() {
            @Override
            public List<UserResponse> listUsers() {
                return Collections.emptyList();
            }

            @Override
            public UserResponse getUserById(Long id) {
                return null;
            }

            @Override
            public UserResponse getUserByUsername(String username) {
                return null;
            }

            @Override
            public List<RoleResponse> listRoles() {
                return Collections.emptyList();
            }

            @Override
            public RoleResponse getRoleById(Long id) {
                return null;
            }

            @Override
            public List<PermissionResponse> listPermissions() {
                return Collections.emptyList();
            }

            @Override
            public PermissionResponse getPermissionById(Long id) {
                return null;
            }

            @Override
            public List<RoleResponse> getRolesByUserId(Long userId) {
                return List.of();
            }

            @Override
            public List<PermissionResponse> getPermissionsByUserId(Long userId) {
                return List.of();
            }
        };
    }
}

package com.hnhegui.hc.config;

import com.hc.framework.satoken.handler.SaPermissionProvider;
import com.hnhegui.hc.feign.UserFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 订单服务权限数据提供者 - 通过 Feign 调用用户服务获取角色和权限
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SaPermissionProviderImpl implements SaPermissionProvider {

    private final UserFeignClient userFeignClient;

    @Override
    public List<String> getRoles(Long userId) {
        try {
            return userFeignClient.getRoleCodesByUserId(userId);
        } catch (Exception e) {
            log.error("获取用户角色失败, userId={}", userId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<String> getPermissions(Long userId) {
        try {
            return userFeignClient.getPermissionCodesByUserId(userId);
        } catch (Exception e) {
            log.error("获取用户权限失败, userId={}", userId, e);
            return Collections.emptyList();
        }
    }
}

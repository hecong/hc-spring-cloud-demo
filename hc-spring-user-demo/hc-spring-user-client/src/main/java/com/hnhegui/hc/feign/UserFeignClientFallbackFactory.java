package com.hnhegui.hc.feign;

import com.hnhegui.hc.feign.response.UserDTO;
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
            public List<UserDTO> listUsers() {
                return Collections.emptyList();
            }

            @Override
            public UserDTO getUserById(Long id) {
                return null;
            }

            @Override
            public UserDTO getUserByUsername(String username) {
                return null;
            }

        };
    }
}

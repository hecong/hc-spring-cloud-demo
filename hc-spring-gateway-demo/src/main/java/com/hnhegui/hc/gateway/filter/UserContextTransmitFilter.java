package com.hnhegui.hc.gateway.filter;

import cn.dev33.satoken.reactor.context.SaReactorSyncHolder;
import cn.dev33.satoken.stp.StpUtil;
import com.hnhegui.hc.context.constant.UserContextConstant;
import com.hnhegui.hc.context.core.UserContext;
import com.hnhegui.hc.context.core.UserContextHolder;
import com.hnhegui.hc.context.util.UserContextEncryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

/**
 * 网关用户上下文转发过滤器
 * 从 Sa-Token 提取用户信息，加密后写入请求 Header
 *
 * @author hecong
 * @since 2026/4/10
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 100)
public class UserContextTransmitFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        try {
            // 1. 设置 Sa-Token 上下文
            SaReactorSyncHolder.setContext(exchange);

            // 2. 检查是否已登录
            if (!StpUtil.isLogin()) {
                return chain.filter(exchange)
                    // 登录态不成立也要清理
                    .doFinally(s -> SaReactorSyncHolder.clearContext());
            }

            // 3. 构建用户上下文
            UserContext userContext = buildUserContext();

            // 4. 加密
            String encrypted = UserContextEncryptUtil.encrypt(userContext);

            // 5. 改写请求头
            ServerHttpRequest modifiedRequest = request.mutate()
                .header(UserContextConstant.DEFAULT_HEADER_NAME, encrypted)
                .header(UserContextConstant.DEFAULT_ENCRYPTED_HEADER_NAME, UserContextConstant.ENCRYPTED_VALUE)
                .build();

            ServerWebExchange modifiedExchange = exchange.mutate()
                .request(modifiedRequest)
                .build();

            // 6. 正常执行 + 最终清理
            return chain.filter(modifiedExchange)
                .doFinally(signalType -> {
                    // 清理两个上下文！！！
                    UserContextHolder.clear();
                    SaReactorSyncHolder.clearContext();
                });

        } catch (Exception e) {
            log.error("[用户上下文] 网关转发失败", e);
            // 异常分支也必须清理
            SaReactorSyncHolder.clearContext();
            return chain.filter(exchange);
        }
    }

    /**
     * 从 Sa-Token 构建用户上下文
     */
    private UserContext buildUserContext() {
        Object loginId = StpUtil.getLoginId();
        Long userId = null;
        if (loginId instanceof Long) {
            userId = (Long) loginId;
        } else if (loginId instanceof String) {
            try {
                userId = Long.parseLong((String) loginId);
            } catch (NumberFormatException ignored) {
            }
        } else if (loginId instanceof Integer) {
            userId = ((Integer) loginId).longValue();
        }

        List<String> roles = StpUtil.getRoleList();
        if (roles == null) {
            roles = Collections.emptyList();
        }

        List<String> permissions = StpUtil.getPermissionList();
        if (permissions == null) {
            permissions = Collections.emptyList();
        }

        return UserContext.builder()
            .userId(userId)
            .roles(roles)
            .permissions(permissions)
            .loginType(StpUtil.getLoginType())
            .token(StpUtil.getTokenValue())
            .build();
    }
}
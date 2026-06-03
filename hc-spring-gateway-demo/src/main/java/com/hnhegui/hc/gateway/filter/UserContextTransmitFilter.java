package com.hnhegui.hc.gateway.filter;

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

import static com.hnhegui.hc.common.constant.CommonConstant.USER_CONTEXT;

/**
 * 网关用户上下文转发过滤器
 * 在 Sa-Token 校验通过后，从 Sa-Token 提取用户信息，加密后写入请求 Header 透传给下游服务
 * 必须运行在 SaReactorFilter 之后（框架的 SaReactorFilter 无 @Order，默认为 LOWEST_PRECEDENCE）
 *
 * @author hecong
 * @since 2026/4/10
 */
@Slf4j
@Component
@Order(Ordered.LOWEST_PRECEDENCE - 100)
public class UserContextTransmitFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        try {
            // 1. 检查是否已登录（此时 SaReactorFilter 已完成 Token 校验）
            if (!StpUtil.isLogin()) {
                return chain.filter(exchange);
            }

            // 2. 构建用户上下文
            UserContext userContext = buildUserContext();

            // 3. 加密
            String encrypted = UserContextEncryptUtil.encrypt(userContext);

            // 4. 改写请求头
            ServerHttpRequest modifiedRequest = request.mutate()
                .header(UserContextConstant.DEFAULT_HEADER_NAME, encrypted)
                .header(UserContextConstant.DEFAULT_ENCRYPTED_HEADER_NAME, UserContextConstant.ENCRYPTED_VALUE)
                .build();

            ServerWebExchange modifiedExchange = exchange.mutate()
                .request(modifiedRequest)
                .build();

            // 5. 正常执行 + 最终清理
            return chain.filter(modifiedExchange)
                .doFinally(signalType -> UserContextHolder.clear());

        } catch (Exception e) {
            log.error("[用户上下文] 网关转发失败", e);
            UserContextHolder.clear();
            return chain.filter(exchange);
        }
    }

    /**
     * 从 Sa-Token 构建用户上下文
     * 只传递基础身份信息，角色/权限由下游服务自行解析
     */
    private UserContext buildUserContext() {
        Object loginId = StpUtil.getLoginId();
        Object sessionContext = StpUtil.getSession().get(USER_CONTEXT);
        if (sessionContext != null) {
            return (UserContext) sessionContext;
        }
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

        return UserContext.builder()
            .userId(userId)
            .loginType(StpUtil.getLoginType())
            .token(StpUtil.getTokenValue())
            .build();
    }
}
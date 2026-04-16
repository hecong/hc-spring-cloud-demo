package com.hnhegui.hc.gateway.config;

import cn.dev33.satoken.reactor.context.SaReactorSyncHolder;
import cn.dev33.satoken.stp.StpInterface;
import com.hc.framework.redis.util.RedisCacheUtils;
import com.hc.framework.satoken.gateway.handler.SaGatewayDynamicRouteProvider;
import com.hnhegui.hc.gateway.listener.SaRouteRefreshListener;
import com.hnhegui.hc.gateway.provider.SaGatewayDynamicRouteProviderImpl;
import com.hnhegui.hc.gateway.provider.SaTokenGatewayStpInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import reactor.core.publisher.Mono;

/**
 * 网关全局配置
 */
@Slf4j
@Configuration
public class GatewayConfig {

    /**
     * 全局日志过滤器
     */
    @Bean
    @Order(-1)
    public GlobalFilter globalLogFilter() {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            String method = exchange.getRequest().getMethod().name();
            SaReactorSyncHolder.setContext(exchange);
            log.info("[网关请求] {} {}", method, path);

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("[网关响应] {} {} 完成", method, path);
                SaReactorSyncHolder.clearContext();
            }));
        };
    }


    /**
     * 网关动态路由提供者
     */
    @Bean
    public SaGatewayDynamicRouteProvider saGatewayDynamicRouteProvider(RedisCacheUtils redisCacheUtils) {
        return new SaGatewayDynamicRouteProviderImpl(redisCacheUtils);
    }

    /**
     * 网关动态路由提供者
     */
    @Bean
    public StpInterface saTokenGatewayStpInterface() {
        return new SaTokenGatewayStpInterface();
    }


}

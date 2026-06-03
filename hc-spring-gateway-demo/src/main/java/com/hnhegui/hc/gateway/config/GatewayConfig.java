package com.hnhegui.hc.gateway.config;

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
     * SaReactorSyncHolder 生命周期由框架 SaReactorFilter 管理，此处不重复处理
     */
    @Bean
    @Order(-1)
    public GlobalFilter globalLogFilter() {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            String method = exchange.getRequest().getMethod().name();
            log.info("[网关请求] {} {}", method, path);

            return chain.filter(exchange).then(Mono.fromRunnable(() ->
                log.info("[网关响应] {} {} 完成", method, path)
            ));
        };
    }
}

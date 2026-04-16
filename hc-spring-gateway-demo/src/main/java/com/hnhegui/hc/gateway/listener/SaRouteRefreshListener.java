package com.hnhegui.hc.gateway.listener;

import com.hc.framework.satoken.gateway.handler.SaGatewayDynamicRouteProvider;
import com.hnhegui.hc.common.constant.CommonCacheConstants;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

/**
 * Redis Pub/Sub 路由刷新监听器
 * 当路由配置变更时，接收通知并刷新本地缓存
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class SaRouteRefreshListener {

    private final RedissonClient redissonClient;

    /**
     * Redis 路由刷新频道
     */
    public static final String ROUTE_REFRESH_CHANNEL = CommonCacheConstants.GATEWAY_ROUTE_REFRESH_CHANNEL;

    private final SaGatewayDynamicRouteProvider saGatewayDynamicRouteProvider;


    @PostConstruct
    public void init() {
        log.info("初始化路由刷新监听器");
        RTopic topic = redissonClient.getTopic(ROUTE_REFRESH_CHANNEL);
        topic.addListener(String.class, (channel, msg) -> {
            log.info("收到路由刷新通知，开始刷新路由缓存");
            saGatewayDynamicRouteProvider.clearCache();
            log.info("路由缓存刷新完成");
        });
    }
}

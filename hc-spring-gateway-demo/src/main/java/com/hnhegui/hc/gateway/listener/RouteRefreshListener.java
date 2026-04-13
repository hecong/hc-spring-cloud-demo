package com.hnhegui.hc.gateway.listener;

import com.hc.framework.satoken.gateway.handler.SaGatewayDynamicRouteProvider;
import com.hnhegui.hc.common.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * Redis Pub/Sub 路由刷新监听器
 * 当路由配置变更时，接收通知并刷新本地缓存
 */
@Slf4j
@Component
public class RouteRefreshListener implements MessageListener {

    /**
     * Redis 路由刷新频道
     */
    public static final String ROUTE_REFRESH_CHANNEL = CacheConstant.GATEWAY_ROUTE_REFRESH_CHANNEL;

    private final SaGatewayDynamicRouteProvider routeProvider;

    public RouteRefreshListener(SaGatewayDynamicRouteProvider routeProvider) {
        this.routeProvider = routeProvider;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String body = new String(message.getBody());
        log.info("收到路由刷新消息，channel: {}, body: {}", channel, body);

        try {
            // 清除本地缓存，下次请求会重新加载
            routeProvider.clearCache();
            log.info("路由缓存已刷新");
        } catch (Exception e) {
            log.error("刷新路由缓存失败", e);
        }
    }
}

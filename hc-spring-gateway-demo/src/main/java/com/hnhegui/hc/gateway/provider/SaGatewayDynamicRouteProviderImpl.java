package com.hnhegui.hc.gateway.provider;

import com.hc.framework.redis.util.RedisCacheUtils;
import com.hc.framework.satoken.gateway.handler.DynamicAuthRoute;
import com.hc.framework.satoken.gateway.handler.SaGatewayDynamicRouteProvider;

import java.util.List;

public class SaGatewayDynamicRouteProviderImpl implements SaGatewayDynamicRouteProvider {


    private final RedisCacheUtils redisCacheUtils;


    private volatile RouteMatchIndex cachedIndex;

    public SaGatewayDynamicRouteProviderImpl(RedisCacheUtils redisCacheUtils) {
        this.redisCacheUtils = redisCacheUtils;
    }

    @Override
    public List<DynamicAuthRoute> loadRoutes() {
        // 从 Redis 或远程服务加载
        String key = "sa:menu:";
        return redisCacheUtils.lRange(key, 0, -1);
    }

    @Override
    public RouteMatchIndex getOrCreateMatchIndex() {
        if (cachedIndex == null) {
            synchronized (this) {
                if (cachedIndex == null) {
                    cachedIndex = buildMatchIndex(loadRoutes());
                }
            }
        }
        return cachedIndex;
    }

    @Override
    public void clearCache() {
        cachedIndex = null;
    }
}
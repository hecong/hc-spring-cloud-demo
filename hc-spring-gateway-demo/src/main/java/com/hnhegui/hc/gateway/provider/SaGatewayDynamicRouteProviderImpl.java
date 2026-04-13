package com.hnhegui.hc.gateway.provider;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.hc.framework.common.model.DynamicAuthRoute;
import com.hc.framework.redis.util.RedisCacheUtils;
import com.hc.framework.satoken.gateway.handler.SaGatewayDynamicRouteProvider;
import com.hnhegui.hc.common.constant.CommonCacheConstants;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 动态路由提供者实现
 * 使用 Caffeine 本地缓存，10分钟过期，兼顾性能和实时性
 */
@Slf4j
public class SaGatewayDynamicRouteProviderImpl implements SaGatewayDynamicRouteProvider {

    private final RedisCacheUtils redisCacheUtils;

    /**
     * Caffeine 本地缓存，10分钟过期
     * 即使 Redis Pub/Sub 监听失效，10分钟后也会重新从 Redis 加载
     */
    private final Cache<String, RouteMatchIndex> routeCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(1)
            .build();

    private static final String CACHE_KEY = "route_match_index";

    public SaGatewayDynamicRouteProviderImpl(RedisCacheUtils redisCacheUtils) {
        this.redisCacheUtils = redisCacheUtils;
    }

    @Override
    public List<DynamicAuthRoute> loadRoutes() {
        // 从 Redis 或远程服务加载
        return redisCacheUtils.lRange(CommonCacheConstants.DYNAMIC_AUTH, 0, -1);
    }

    @Override
    public RouteMatchIndex getOrCreateMatchIndex() {
        // 从缓存获取，如果不存在则加载
        return routeCache.get(CACHE_KEY, key -> {
            log.debug("构建路由匹配索引");
            return buildMatchIndex(loadRoutes());
        });
    }

    @Override
    public void clearCache() {
        log.info("清除路由本地缓存");
        routeCache.invalidateAll();
    }
}
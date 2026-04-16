package com.hnhegui.hc.common.constant;

/**
 * @author hecong
 * @since 2026/4/13 16:21
 */
public interface CommonCacheConstants {

    /**
     * 缓存 Key 前缀
     */
    String CACHE_PREFIX = "hc:";

    /**
     * 动态鉴权
     */
    String DYNAMIC_AUTH = CACHE_PREFIX + "dynamic:auth";

    /**
     * 网关路由刷新 Redis Pub/Sub 频道
     */
    String GATEWAY_ROUTE_REFRESH_CHANNEL = "gateway:route:refresh";

}

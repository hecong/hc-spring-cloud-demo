package com.hnhegui.hc.common.constant;

/**
 * 缓存相关常量
 */
public final class CacheConstant {

    private CacheConstant() {
        // 禁止实例化
    }

    /**
     * 网关路由刷新 Redis Pub/Sub 频道
     */
    public static final String GATEWAY_ROUTE_REFRESH_CHANNEL = "gateway:route:refresh";

    /**
     * 动态权限路由缓存Key
     */
    public static final String DYNAMIC_AUTH_KEY = "dynamic:auth";

}

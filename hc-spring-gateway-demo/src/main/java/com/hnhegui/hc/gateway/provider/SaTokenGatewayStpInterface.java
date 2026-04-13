package com.hnhegui.hc.gateway.provider;

import cn.dev33.satoken.stp.StpInterface;
import com.hc.framework.redis.util.RedisCacheUtils;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.hc.framework.common.constant.CacheConstants.PERMISSIONS_SUFFIX;
import static com.hc.framework.common.constant.CacheConstants.ROLES_SUFFIX;


/**
 * @author hecong
 * @since 2026/4/10 09:20
 */
@RequiredArgsConstructor
public class SaTokenGatewayStpInterface implements StpInterface {

    private final RedisCacheUtils redisCacheUtils;


    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        String key = PERMISSIONS_SUFFIX + loginId;
        return redisCacheUtils.get(key);
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        String key = ROLES_SUFFIX + loginId;
        return redisCacheUtils.get(key);
    }
}

package com.hnhegui.hc.gateway.provider;

import cn.dev33.satoken.stp.StpInterface;
import com.hc.framework.redis.util.RedisCacheUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author hecong
 * @since 2026/4/10 09:20
 */
@RequiredArgsConstructor
@Component
@ConditionalOnBean(RedisCacheUtils.class)
public class SaTokenGatewayStpInterface implements StpInterface {

    private final RedisCacheUtils redisCacheUtils;


    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        String key = "sa:permission:" + loginId;
        return redisCacheUtils.lRange(key, 0, -1);
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        String key = "sa:role:" + loginId;
        return redisCacheUtils.lRange(key, 0, -1);
    }
}

package com.hnhegui.hc.service.auth.impl;

import com.hc.framework.mybatis.model.DataScopeInfo;
import com.hc.framework.mybatis.spi.DataScopeProvider;
import com.hnhegui.hc.context.core.UserContext;
import com.hnhegui.hc.context.core.UserContextHolder;
import com.hnhegui.hc.service.auth.DataScopeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 数据范围提供者默认实现
 *
 * <p>从 UserContext 获取当前登录用户，调用 DataScopeService 计算有效数据范围。
 * 超级管理员直接返回 ALL 权限。</p>
 *
 * @author hecong
 * @since 2026/6/11
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultDataScopeProviderImpl implements DataScopeProvider {

    private final DataScopeService dataScopeService;

    @Override
    public DataScopeInfo getDataScope(Long userId, String permissionCode) {
        // 超级管理员直接返回 ALL
        UserContext ctx = UserContextHolder.get();
        if (ctx != null && Boolean.TRUE.equals(ctx.getIsSuperAdmin())) {
            return DataScopeInfo.builder().all(true).build();
        }

        return dataScopeService.getOrComputeDataScope(userId, permissionCode);
    }
}

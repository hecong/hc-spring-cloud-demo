package com.hnhegui.hc.service.auth;

import com.hc.framework.common.model.DataScopeInfo;
import com.hc.framework.common.spi.DataScopeProvider;
import com.hnhegui.hc.context.core.UserContext;
import com.hnhegui.hc.context.core.UserContextHolder;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * DataScopeProvider SPI 适配器
 *
 * <p>通过 ApplicationContext 延迟获取 DataScopeService，打破
 * {@code dataPermissionInterceptor → DataScopeProvider → mappers → sqlSessionFactory} 循环依赖。</p>
 *
 * <p>框架在 sqlSessionFactory 初始化期间创建 dataPermissionInterceptor，
 * 此时注入一个轻量适配器，实际 DataScopeService 在首次方法调用时才从容器获取。</p>
 *
 * @author hecong
 * @since 2026/6/16
 */
@Component
public class DataScopeProviderAdapter implements DataScopeProvider {

    private final ApplicationContext applicationContext;

    public DataScopeProviderAdapter(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public DataScopeInfo getDataScope(Long userId, String permissionCode) {
        // 超级管理员直接返回 ALL，无需查库
        UserContext ctx = UserContextHolder.get();
        if (ctx != null && Boolean.TRUE.equals(ctx.getIsSuperAdmin())) {
            return DataScopeInfo.builder().all(true).build();
        }
        // 延迟获取 DataScopeService，打破初始化阶段的循环依赖
        DataScopeService service = applicationContext.getBean(DataScopeService.class);
        return service.getOrComputeDataScope(userId, permissionCode);
    }
}

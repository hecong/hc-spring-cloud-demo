package com.hnhegui.hc.context.spi;

import com.hc.framework.common.spi.UserContextPermissionProvider;
import com.hnhegui.hc.context.core.UserContext;
import com.hnhegui.hc.context.core.UserContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 基于请求上下文的权限数据提供者
 *
 * <p>从 UserContextHolder（Gateway 透传的用户上下文）中获取角色和权限。</p>
 * <p>每次请求 Gateway 都会从 Sa-Token Session 读取最新的 UserContext 并透传，</p>
 * <p>因此数据始终是最新的，无需跨服务 Redis 缓存同步。</p>
 *
 * <p>当不在 Web 请求上下文中时（如定时任务、MQ 消费者），返回 null，</p>
 * <p>框架会自动回退到 Redis 缓存或 SaPermissionProvider 查询。</p>
 */
@Component
public class UserContextPermissionProviderImpl implements UserContextPermissionProvider {

    @Override
    public List<String> getCurrentUserRoles() {
        UserContext ctx = UserContextHolder.get();
        return ctx != null ? ctx.getRoles() : null;
    }

    @Override
    public List<String> getCurrentUserPermissions() {
        UserContext ctx = UserContextHolder.get();
        return ctx != null ? ctx.getPermissions() : null;
    }
}

package com.hnhegui.hc.service.auth;

import cn.dev33.satoken.stp.StpUtil;
import com.hc.framework.satoken.handler.SaPermissionProvider;
import com.hc.framework.satoken.util.SaTokenHelper;
import com.hnhegui.hc.context.core.UserContext;
import com.hnhegui.hc.mapper.user.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static com.hnhegui.hc.common.constant.CommonConstant.USER_CONTEXT;

/**
 * 权限缓存刷新服务
 *
 * <p>权限变更后，负责：</p>
 * <ol>
 *   <li>清除本服务 Redis 权限缓存</li>
 *   <li>更新 Sa-Token Session 中的 UserContext（Gateway 下次请求会读到最新数据）</li>
 * </ol>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionCacheRefreshService {

    private final SaTokenHelper saTokenHelper;
    private final SaPermissionProvider permissionProvider;
    private final UserRoleMapper userRoleMapper;

    /**
     * 刷新单个用户的权限缓存
     *
     * @param userId 用户ID
     */
    public void refreshUser(Long userId) {
        List<String> roles = permissionProvider.getRoles(userId);
        List<String> permissions = permissionProvider.getPermissions(userId);
        saTokenHelper.refreshPermissionCache(userId, roles, permissions);
        updateSession(userId, roles, permissions);
    }

    /**
     * 刷新某个角色下所有用户的权限缓存
     *
     * @param roleId 角色ID
     */
    public void refreshByRoleId(Long roleId) {
        List<Long> userIds = userRoleMapper.selectUserIdsByRoleId(roleId);
        for (Long userId : userIds) {
            refreshUser(userId);
        }
        log.info("已刷新角色 {} 下 {} 个用户的权限缓存", roleId, userIds.size());
    }

    /**
     * 刷新指定用户列表的权限缓存
     *
     * @param userIds 用户ID列表
     */
    public void refreshUsers(List<Long> userIds) {
        for (Long userId : userIds) {
            refreshUser(userId);
        }
        log.info("已刷新 {} 个用户的权限缓存", userIds.size());
    }

    /**
     * 更新 Sa-Token Session 中的 UserContext
     *
     * <p>Gateway 每次请求会从 Session 读取 UserContext 并透传给下游服务。</p>
     * <p>更新 Session 后，Gateway 下次请求就能拿到最新的角色/权限。</p>
     */
    private void updateSession(Long userId, List<String> roles, List<String> permissions) {
        try {
            cn.dev33.satoken.session.SaSession session = StpUtil.getSessionByLoginId(userId, false);
            if (session == null) {
                return;
            }
            Object ctxObj = session.get(USER_CONTEXT);
            if (ctxObj instanceof UserContext ctx) {
                ctx.setRoles(roles != null ? roles : Collections.emptyList());
                ctx.setPermissions(permissions != null ? permissions : Collections.emptyList());
                session.set(USER_CONTEXT, ctx);
                log.debug("已更新 Session 中的 UserContext: userId={}", userId);
            }
        } catch (Exception e) {
            // 用户可能未登录，忽略
            log.debug("更新 Session 失败（用户可能未登录）: userId={}, error={}", userId, e.getMessage());
        }
    }
}

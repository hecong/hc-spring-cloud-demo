package com.hnhegui.hc.service.auth;

import cn.dev33.satoken.stp.StpUtil;
import com.hc.framework.satoken.util.SaTokenHelper;
import com.hnhegui.hc.context.core.UserContext;
import com.hnhegui.hc.entity.permission.Permission;
import com.hnhegui.hc.entity.role.Role;
import com.hnhegui.hc.event.RolePermissionChangedEvent;
import com.hnhegui.hc.mapper.permission.PermissionMapper;
import com.hnhegui.hc.mapper.role.RoleMapper;
import com.hnhegui.hc.mapper.role.RolePermissionMapper;
import com.hnhegui.hc.mapper.user.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.hnhegui.hc.common.constant.CommonConstant.USER_CONTEXT;

/**
 * 权限缓存刷新服务
 *
 * <p>监听角色权限变更事件，负责：</p>
 * <ol>
 *   <li>刷新 Sa-Token 权限缓存</li>
 *   <li>更新 Session 中的 UserContext</li>
 * </ol>
 *
 * @author hecong
 * @since 2026/6/11
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionCacheRefreshService {

    private final SaTokenHelper saTokenHelper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final PermissionMapper permissionMapper;

    /**
     * 监听角色权限分配事件
     */
    @EventListener
    public void onRolePermAssigned(RolePermissionChangedEvent event) {
        if (event.getChangeType() != RolePermissionChangedEvent.ChangeType.PERM_ASSIGNED) {
            return;
        }
        Long roleId = event.getRoleId();
        List<Long> userIds = userRoleMapper.selectUserIdsByRoleId(roleId);
        for (Long userId : userIds) {
            refreshUser(userId);
        }
        log.info("已刷新角色 {} 下 {} 个用户的权限缓存", roleId, userIds.size());
    }

    /**
     * 监听角色删除事件
     */
    @EventListener
    public void onRoleDeleted(RolePermissionChangedEvent event) {
        if (event.getChangeType() != RolePermissionChangedEvent.ChangeType.ROLE_DELETED) {
            return;
        }
        List<Long> userIds = event.getUserIds();
        if (userIds != null) {
            for (Long userId : userIds) {
                refreshUser(userId);
            }
            log.info("已刷新 {} 个用户的权限缓存", userIds.size());
        }
    }

    /**
     * 刷新单个用户的权限缓存
     */
    public void refreshUser(Long userId) {
        List<String> roles = getRoleCodesByUserId(userId);
        List<String> permissions = getPermissionCodesByUserId(userId);
        saTokenHelper.refreshPermissionCache(userId, roles, permissions);
        updateSession(userId, roles, permissions);
    }

    /**
     * 批量刷新用户的权限缓存
     */
    public void refreshUsers(List<Long> userIds) {
        for (Long userId : userIds) {
            refreshUser(userId);
        }
        log.info("已批量刷新 {} 个用户的权限缓存", userIds.size());
    }

    // ====================== 内部查询方法 ======================

    /**
     * 根据用户ID查询角色编码列表
     */
    private List<String> getRoleCodesByUserId(Long userId) {
        List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);
        if (roleIds.isEmpty()) {
            return Collections.emptyList();
        }
        return roleMapper.selectByIds(roleIds).stream()
            .map(Role::getCode)
            .collect(Collectors.toList());
    }

    /**
     * 根据用户ID查询权限编码列表
     */
    private List<String> getPermissionCodesByUserId(Long userId) {
        List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);
        if (roleIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> permissionIds = new HashSet<>(rolePermissionMapper.selectPermissionIdsByRoleIds(roleIds))
            .stream().toList();
        if (permissionIds.isEmpty()) {
            return Collections.emptyList();
        }
        return permissionMapper.selectByIds(permissionIds).stream()
            .map(Permission::getCode)
            .collect(Collectors.toList());
    }

    // ====================== Session 更新 ======================

    /**
     * 更新 Sa-Token Session 中的 UserContext
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
            log.debug("更新 Session 失败（用户可能未登录）: userId={}, error={}", userId, e.getMessage());
        }
    }
}

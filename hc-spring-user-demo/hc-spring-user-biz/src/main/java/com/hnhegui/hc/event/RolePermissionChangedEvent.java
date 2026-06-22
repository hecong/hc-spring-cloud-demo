package com.hnhegui.hc.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * 角色权限变更事件
 *
 * <p>当角色的权限分配发生变化时发布，由 PermissionCacheRefreshService 监听并刷新受影响用户的权限缓存。</p>
 *
 * @author hecong
 * @since 2026/6/16
 */
@Getter
public class RolePermissionChangedEvent extends ApplicationEvent {

    /** 变更类型 */
    private final ChangeType changeType;
    /** 角色ID（assignPermissions 场景） */
    private final Long roleId;
    /** 受影响的用户ID列表（deleteRole 场景） */
    private final List<Long> userIds;

    public enum ChangeType {
        /** 角色权限重新分配 */
        PERM_ASSIGNED,
        /** 角色被删除 */
        ROLE_DELETED
    }

    private RolePermissionChangedEvent(Object source, ChangeType changeType, Long roleId, List<Long> userIds) {
        super(source);
        this.changeType = changeType;
        this.roleId = roleId;
        this.userIds = userIds;
    }

    /** 权限分配事件 */
    public static RolePermissionChangedEvent ofPermAssigned(Object source, Long roleId) {
        return new RolePermissionChangedEvent(source, ChangeType.PERM_ASSIGNED, roleId, null);
    }

    /** 角色删除事件 */
    public static RolePermissionChangedEvent ofRoleDeleted(Object source, List<Long> userIds) {
        return new RolePermissionChangedEvent(source, ChangeType.ROLE_DELETED, null, userIds);
    }
}

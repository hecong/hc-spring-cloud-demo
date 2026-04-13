package com.hnhegui.hc.context.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 用户上下文对象
 * 包含用户基础信息，用于跨服务传递
 *
 * @author hecong
 * @since 2026/4/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserContext implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 角色列表
     */
    private List<String> roles;

    /**
     * 权限列表
     */
    private List<String> permissions;

    /**
     * 登录类型
     */
    private String loginType;

    /**
     * Token
     */
    private String token;

    /**
     * 登录时间
     */
    private Long loginTime;

    /**
     * 客户端IP
     */
    private String clientIp;

    /**
     * 判断用户是否已登录
     */
    @JsonIgnore
    public boolean isLogin() {
        return userId != null && userId > 0;
    }

    /**
     * 判断用户是否具有指定角色
     */
    @JsonIgnore
    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    /**
     * 判断用户是否具有指定权限
     */
    @JsonIgnore
    public boolean hasPermission(String permission) {
        return permissions != null && permissions.contains(permission);
    }
}

package com.hnhegui.hc.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.hc.framework.satoken.util.SaTokenHelper;
import com.hnhegui.hc.context.core.UserContext;
import com.hnhegui.hc.context.core.UserContextHolder;
import com.hnhegui.hc.dto.UserResponse;
import com.hnhegui.hc.service.AuthService;
import com.hnhegui.hc.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hnhegui.hc.common.constant.CommonConstant.USER_CONTEXT;

/**
 * 认证服务实现
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final SaTokenHelper saTokenHelper;

    public AuthServiceImpl(UserService userService, SaTokenHelper saTokenHelper) {
        this.userService = userService;
        this.saTokenHelper = saTokenHelper;
    }

    @Override
    public Map<String, Object> login(String username, String password) {
        UserResponse userResponse = userService.getUserByUsername(username);
        if (userResponse == null) {
            return null;
        }

        // 执行登录
        saTokenHelper.login(userResponse.getId());

        // 构建用户上下文
        UserContext userContext = UserContext.builder()
                .userId(userResponse.getId())
                .username(userResponse.getUsername())
                .nickname(userResponse.getName())
                .email(userResponse.getEmail())
                .phone(userResponse.getPhone())
                .build();

        // 获取角色和权限
        List<String> roleList = saTokenHelper.getRoleList();
        List<String> permissionList = saTokenHelper.getPermissionList();
        userContext.setRoles(roleList);
        userContext.setPermissions(permissionList);

        // 存储到Session和上下文
        StpUtil.getSession().set(USER_CONTEXT, userContext);
        UserContextHolder.set(userContext);

        // 组装返回数据
        Map<String, Object> data = new HashMap<>();
        data.put("token", saTokenHelper.getTokenValue());
        data.put("tokenName", saTokenHelper.getTokenName());
        data.put("user", userResponse);

        return data;
    }

    @Override
    public void logout() {
        saTokenHelper.logout();
    }

    @Override
    public Map<String, Object> getCurrentUserInfo() {
        UserContext userContext = UserContextHolder.get();
        log.info("UserContext: {}", userContext);

        Long userId = UserContextHolder.getUserId();
        UserResponse userResponse = userService.getUserById(userId);

        Map<String, Object> data = new HashMap<>();
        data.put("user", userResponse);
        data.put("roles", saTokenHelper.getRoleList());
        data.put("permissions", saTokenHelper.getPermissionList());

        return data;
    }
}

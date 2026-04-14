package com.hnhegui.hc.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.hc.framework.satoken.util.SaTokenHelper;
import com.hc.framework.web.model.Result;
import com.hnhegui.hc.context.core.UserContext;
import com.hnhegui.hc.context.core.UserContextHolder;
import com.hnhegui.hc.dto.UserResponse;
import com.hnhegui.hc.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final SaTokenHelper saTokenHelper;

    public AuthController(UserService userService, SaTokenHelper saTokenHelper) {
        this.userService = userService;
        this.saTokenHelper = saTokenHelper;
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        UserResponse userResponse = userService.getUserByUsername(username);
        if (userResponse == null) {
            return Result.error("用户名或密码错误");
        }
        saTokenHelper.login(userResponse.getId());
        UserContext userContext = UserContext.builder().userId(userResponse.getId()).username(userResponse.getUsername()).nickname(userResponse.getName()).email(userResponse.getEmail()).phone(userResponse.getPhone()).build();
        List<String> roleList = saTokenHelper.getRoleList();
        List<String> permissionList = saTokenHelper.getPermissionList();
        userContext.setRoles(roleList);
        userContext.setPermissions(permissionList);
        StpUtil.getSession().set("userContext", userContext);
        UserContextHolder.set(userContext);

        Map<String, Object> data = new HashMap<>();
        data.put("token", saTokenHelper.getTokenValue());
        data.put("tokenName", saTokenHelper.getTokenName());
        data.put("user", userResponse);
        return Result.success(data);
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        saTokenHelper.logout();
        return Result.success();
    }

    @PostMapping("/info")
    public Result<Map<String, Object>> getInfo() {
        UserContext userContext = UserContextHolder.get();
        log.info("UserContext: {}", userContext);
        Long userId = UserContextHolder.getUserId();
//        Long userId = saTokenHelper.getCurrentUserId();
        UserResponse userResponse = userService.getUserById(userId);
        Map<String, Object> data = new HashMap<>();
        data.put("user", userResponse);
        data.put("roles", saTokenHelper.getRoleList());
        data.put("permissions", saTokenHelper.getPermissionList());
        return Result.success(data);
    }
}
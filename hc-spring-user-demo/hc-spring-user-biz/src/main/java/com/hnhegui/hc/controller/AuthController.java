package com.hnhegui.hc.controller;

import com.hc.framework.satoken.util.SaTokenHelper;
import com.hc.framework.web.model.Result;
import com.hnhegui.hc.dto.UserResponse;
import com.hnhegui.hc.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

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
        saTokenHelper.login(1L);
        saTokenHelper.getRoleList();
        saTokenHelper.getPermissionList();
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
        if (!saTokenHelper.isLogin()) {
            return Result.error(401, "未登录");
        }
        Long userId = saTokenHelper.getCurrentUserId();
        UserResponse userResponse = userService.getUserById(userId);
        Map<String, Object> data = new HashMap<>();
        data.put("user", userResponse);
        data.put("roles", saTokenHelper.getRoleList());
        data.put("permissions", saTokenHelper.getPermissionList());
        return Result.success(data);
    }
}
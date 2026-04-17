package com.hnhegui.hc.controller.auth;

import com.hc.framework.web.model.Result;
import com.hnhegui.hc.service.user.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");

        Map<String, Object> data = authService.login(username, password);
        if (data == null) {
            return Result.error("用户名或密码错误");
        }
        return Result.success(data);
    }

    /**
     * 登出
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }

    /**
     * 获取当前用户信息
     */

    @PostMapping("/info")
    public Result<Map<String, Object>> getInfo() {
        Map<String, Object> data = authService.getCurrentUserInfo();
        return Result.success(data);
    }
}
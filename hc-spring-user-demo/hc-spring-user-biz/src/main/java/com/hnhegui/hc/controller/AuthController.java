package com.hnhegui.hc.controller;

import com.hc.framework.web.model.Result;
import com.hnhegui.hc.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

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

    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }

    @PostMapping("/info")
    public Result<Map<String, Object>> getInfo() {
        Map<String, Object> data = authService.getCurrentUserInfo();
        return Result.success(data);
    }
}
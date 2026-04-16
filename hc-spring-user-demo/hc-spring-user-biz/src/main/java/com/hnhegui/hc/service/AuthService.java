package com.hnhegui.hc.service;

import com.hnhegui.hc.dto.UserResponse;

import java.util.Map;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录结果（包含token、用户信息等）
     */
    Map<String, Object> login(String username, String password);

    /**
     * 用户登出
     */
    void logout();

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息（包含用户详情、角色、权限等）
     */
    Map<String, Object> getCurrentUserInfo();
}

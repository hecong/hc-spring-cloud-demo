package com.hnhegui.hc.service.user;

import com.hnhegui.hc.bo.auth.CurrentUserInfoBO;
import com.hnhegui.hc.bo.auth.LoginResultBO;
import com.hnhegui.hc.bo.enterprise.EnterpriseBO;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * C端密码登录
     *
     * @param account  账号（手机号/邮箱/用户名）
     * @param password 密码
     * @param loginIp  登录IP
     * @param device   登录设备
     * @return 登录结果
     */
    LoginResultBO cPasswordLogin(String account, String password, String loginIp, String device);

    /**
     * C端验证码登录
     *
     * @param target  发送目标（手机号/邮箱）
     * @param code    验证码
     * @param loginIp 登录IP
     * @param device  登录设备
     * @return 登录结果
     */
    LoginResultBO cCodeLogin(String target, String code, String loginIp, String device);

    /**
     * B端密码登录
     *
     * @param enterpriseCode 企业编码
     * @param username       用户名
     * @param password       密码
     * @param loginIp        登录IP
     * @param device         登录设备
     * @return 登录结果
     */
    LoginResultBO bPasswordLogin(String enterpriseCode, String username, String password, String loginIp, String device);

    /**
     * B端验证码登录
     *
     * @param target         发送目标（手机号/邮箱）
     * @param code           验证码
     * @param enterpriseCode 企业编码（可选）
     * @param loginIp        登录IP
     * @param device         登录设备
     * @return 登录结果
     */
    LoginResultBO bCodeLogin(String target, String code, String enterpriseCode, String loginIp, String device);

    /**
     * 校验企业编码是否存在
     *
     * @param enterpriseCode 企业编码
     * @return 企业BO
     */
    EnterpriseBO checkEnterprise(String enterpriseCode);

    /**
     * 获取多身份列表
     *
     * @param phone 手机号
     * @return 登录结果（含身份列表）
     */
    LoginResultBO getIdentityList(String phone);

    /**
     * 选择登录身份
     *
     * @param phone        手机号
     * @param identityType 身份类型：C-个人，B-企业
     * @param enterpriseId 企业ID（B端时必填）
     * @param loginIp      登录IP
     * @param device       登录设备
     * @return 登录结果
     */
    LoginResultBO selectIdentity(String phone, String identityType, Long enterpriseId, String loginIp, String device);

    /**
     * 平台管理员登录
     *
     * @param username 用户名
     * @param password 密码
     * @param loginIp  登录IP
     * @param device   登录设备
     * @return 登录结果
     */
    LoginResultBO platformLogin(String username, String password, String loginIp, String device);

    /**
     * 登出
     */
    void logout();

    /**
     * 获取当前用户信息
     *
     * @return 当前用户信息BO
     */
    CurrentUserInfoBO getCurrentUserInfo();
}

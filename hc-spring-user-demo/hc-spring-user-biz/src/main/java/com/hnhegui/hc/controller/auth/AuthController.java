package com.hnhegui.hc.controller.auth;

import com.hc.framework.web.model.Result;
import com.hc.framework.web.util.ServletUtils;
import com.hnhegui.hc.bo.auth.CurrentUserInfoBO;
import com.hnhegui.hc.bo.auth.LoginResultBO;
import com.hnhegui.hc.bo.enterprise.EnterpriseBO;
import com.hnhegui.hc.controller.auth.converter.AuthConverter;
import com.hnhegui.hc.controller.auth.request.BCodeLoginRequest;
import com.hnhegui.hc.controller.auth.request.BPasswordLoginRequest;
import com.hnhegui.hc.controller.auth.request.CCodeLoginRequest;
import com.hnhegui.hc.controller.auth.request.CPasswordLoginRequest;
import com.hnhegui.hc.controller.auth.request.CheckEnterpriseRequest;
import com.hnhegui.hc.controller.auth.request.IdentityListRequest;
import com.hnhegui.hc.controller.auth.request.PlatformLoginRequest;
import com.hnhegui.hc.controller.auth.request.SelectIdentityRequest;
import com.hnhegui.hc.controller.auth.request.SendCodeRequest;
import com.hnhegui.hc.controller.auth.response.CheckEnterpriseResponse;
import com.hnhegui.hc.controller.auth.response.CurrentUserInfoResponse;
import com.hnhegui.hc.controller.auth.response.LoginResponse;
import com.hnhegui.hc.config.RsaKeyConfig;
import com.hnhegui.hc.service.user.AuthService;
import com.hnhegui.hc.service.verify.VerificationCodeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final VerificationCodeService verificationCodeService;
    private final RsaKeyConfig rsaKeyConfig;

    /**
     * 获取RSA公钥（用于前端加密登录密码）
     */
    @GetMapping("/rsa-public-key")
    public Result<String> getRsaPublicKey() {
        return Result.success(rsaKeyConfig.getPublicKeyBase64());
    }

    /**
     * C端密码登录
     */
    @PostMapping("/c/password-login")
    public Result<LoginResponse> cPasswordLogin(@Valid @RequestBody CPasswordLoginRequest request,
                                                HttpServletRequest httpRequest) {
        String loginIp = ServletUtils.getClientIP(httpRequest);
        String device = httpRequest.getHeader("User-Agent");
        LoginResultBO bo = authService.cPasswordLogin(request.getAccount(), request.getPassword(), loginIp, device);
        return Result.success(AuthConverter.INSTANCE.toLoginResponse(bo));
    }

    /**
     * C端验证码登录
     */
    @PostMapping("/c/code-login")
    public Result<LoginResponse> cCodeLogin(@Valid @RequestBody CCodeLoginRequest request,
                                            HttpServletRequest httpRequest) {
        String loginIp = ServletUtils.getClientIP(httpRequest);
        String device = httpRequest.getHeader("User-Agent");
        LoginResultBO bo = authService.cCodeLogin(request.getTarget(), request.getCode(), loginIp, device);
        return Result.success(AuthConverter.INSTANCE.toLoginResponse(bo));
    }

    /**
     * B端密码登录
     */
    @PostMapping("/b/password-login")
    public Result<LoginResponse> bPasswordLogin(@Valid @RequestBody BPasswordLoginRequest request,
                                                HttpServletRequest httpRequest) {
        String loginIp = ServletUtils.getClientIP(httpRequest);
        String device = httpRequest.getHeader("User-Agent");
        LoginResultBO bo = authService.bPasswordLogin(request.getEnterpriseCode(), request.getUsername(),
                request.getPassword(), loginIp, device);
        return Result.success(AuthConverter.INSTANCE.toLoginResponse(bo));
    }

    /**
     * B端验证码登录
     */
    @PostMapping("/b/code-login")
    public Result<LoginResponse> bCodeLogin(@Valid @RequestBody BCodeLoginRequest request,
                                            HttpServletRequest httpRequest) {
        String loginIp = ServletUtils.getClientIP(httpRequest);
        String device = httpRequest.getHeader("User-Agent");
        LoginResultBO bo = authService.bCodeLogin(request.getTarget(), request.getCode(),
                request.getEnterpriseCode(), loginIp, device);
        return Result.success(AuthConverter.INSTANCE.toLoginResponse(bo));
    }

    /**
     * B端第一步校验企业ID
     */
    @PostMapping("/b/check-enterprise")
    public Result<CheckEnterpriseResponse> checkEnterprise(@Valid @RequestBody CheckEnterpriseRequest request) {
        EnterpriseBO enterpriseBO = authService.checkEnterprise(request.getEnterpriseCode());
        return Result.success(AuthConverter.INSTANCE.toCheckEnterpriseResponse(enterpriseBO));
    }

    /**
     * 平台管理员登录
     */
    @PostMapping("/platform/login")
    public Result<LoginResponse> platformLogin(@Valid @RequestBody PlatformLoginRequest request,
                                               HttpServletRequest httpRequest) {
        String loginIp = ServletUtils.getClientIP(httpRequest);
        String device = httpRequest.getHeader("User-Agent");
        LoginResultBO bo = authService.platformLogin(request.getUsername(), request.getPassword(), loginIp, device);
        return Result.success(AuthConverter.INSTANCE.toLoginResponse(bo));
    }

    /**
     * 获取多身份列表
     */
    @PostMapping("/identity-list")
    public Result<LoginResponse> getIdentityList(@Valid @RequestBody IdentityListRequest request) {
        LoginResultBO bo = authService.getIdentityList(request.getPhone());
        return Result.success(AuthConverter.INSTANCE.toLoginResponse(bo));
    }

    /**
     * 选择登录身份
     */
    @PostMapping("/identity-select")
    public Result<LoginResponse> selectIdentity(@Valid @RequestBody SelectIdentityRequest request,
                                                HttpServletRequest httpRequest) {
        String loginIp = ServletUtils.getClientIP(httpRequest);
        String device = httpRequest.getHeader("User-Agent");
        LoginResultBO bo = authService.selectIdentity(request.getPhone(), request.getIdentityType(),
                request.getEnterpriseId(), loginIp, device);
        return Result.success(AuthConverter.INSTANCE.toLoginResponse(bo));
    }

    /**
     * 发送验证码
     */
    @PostMapping("/code/send")
    public Result<String> sendCode(@Valid @RequestBody SendCodeRequest request, HttpServletRequest httpRequest) {
        String ip = ServletUtils.getClientIP(httpRequest);
        String code = verificationCodeService.sendCode(request.getTarget(), request.getScene(), ip);
        return Result.success(code);
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
    public Result<CurrentUserInfoResponse> getInfo() {
        CurrentUserInfoBO bo = authService.getCurrentUserInfo();
        return Result.success(AuthConverter.INSTANCE.toCurrentUserInfoResponse(bo));
    }
}

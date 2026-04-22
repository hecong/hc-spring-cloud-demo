package com.hnhegui.hc.controller.cuser;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hc.framework.web.model.Result;
import com.hc.framework.mybatis.model.PageData;
import com.hc.framework.web.util.ServletUtils;
import com.hnhegui.hc.bo.cuser.CUserBO;
import com.hnhegui.hc.bo.cuser.CUserThirdPartyBO;
import com.hnhegui.hc.bo.log.LoginLogBO;
import com.hnhegui.hc.bo.log.LoginLogPageQueryBO;
import com.hnhegui.hc.common.enums.UserTypeEnum;
import com.hnhegui.hc.context.core.UserContextHolder;
import com.hnhegui.hc.controller.cuser.converter.CUserConverter;
import com.hnhegui.hc.controller.cuser.request.CUserRegisterRequest;
import com.hnhegui.hc.controller.cuser.request.ChangeEmailRequest;
import com.hnhegui.hc.controller.cuser.request.ChangePasswordRequest;
import com.hnhegui.hc.controller.cuser.request.ChangePhoneRequest;
import com.hnhegui.hc.controller.cuser.request.ResetPasswordRequest;
import com.hnhegui.hc.controller.cuser.request.SetIdentityDefaultRequest;
import com.hnhegui.hc.controller.cuser.request.UnbindThirdPartyRequest;
import com.hnhegui.hc.controller.cuser.request.UpdateProfileRequest;
import com.hnhegui.hc.controller.cuser.response.CUserResponse;
import com.hnhegui.hc.controller.cuser.response.CUserThirdPartyResponse;
import com.hnhegui.hc.controller.log.converter.LoginLogConverter;
import com.hnhegui.hc.controller.log.request.LoginLogPageRequest;
import com.hnhegui.hc.controller.log.response.LoginLogResponse;
import com.hnhegui.hc.service.cuser.CUserService;
import com.hnhegui.hc.service.log.LoginLogService;
import com.hnhegui.hc.service.verify.AccountLockService;
import cn.dev33.satoken.stp.StpUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cuser")
@RequiredArgsConstructor
public class CUserController {

    private final CUserService cUserService;
    private final LoginLogService loginLogService;
    private final AccountLockService accountLockService;

    /**
     * C端用户注册
     */
    @PostMapping("/register")
    public Result<CUserResponse> register(@Valid @RequestBody CUserRegisterRequest request) {
        CUserBO bo = cUserService.register(request.getPhone(), request.getPassword(),
                request.getEmail(), request.getUsername());
        return Result.success(CUserConverter.INSTANCE.toResponse(bo));
    }

    /**
     * 找回密码
     */
    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        cUserService.resetPassword(request.getAccount(), request.getNewPassword());
        return Result.success();
    }

    /**
     * 获取个人信息
     */
    @GetMapping("/profile")
    public Result<CUserResponse> getProfile() {
        Long userId = UserContextHolder.getUserId();
        CUserBO bo = cUserService.getCUserById(userId);
        return Result.success(CUserConverter.INSTANCE.toResponse(bo));
    }

    /**
     * 修改个人信息
     */
    @PutMapping("/profile")
    public Result<CUserResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        Long userId = UserContextHolder.getUserId();
        CUserBO bo = cUserService.updateProfile(userId, request.getNickname(), request.getAvatar(),
                request.getGender(), request.getBirthday());
        return Result.success(CUserConverter.INSTANCE.toResponse(bo));
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Long userId = UserContextHolder.getUserId();
        cUserService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
        return Result.success();
    }

    /**
     * 更换手机号
     */
    @PutMapping("/phone")
    public Result<Void> changePhone(@Valid @RequestBody ChangePhoneRequest request) {
        Long userId = UserContextHolder.getUserId();
        cUserService.changePhone(userId, request.getNewPhone());
        return Result.success();
    }

    /**
     * 更换邮箱
     */
    @PutMapping("/email")
    public Result<Void> changeEmail(@Valid @RequestBody ChangeEmailRequest request) {
        Long userId = UserContextHolder.getUserId();
        cUserService.changeEmail(userId, request.getNewEmail());
        return Result.success();
    }

    /**
     * 查看第三方绑定
     */
    @GetMapping("/third-party")
    public Result<List<CUserThirdPartyResponse>> getThirdPartyBindings() {
        Long userId = UserContextHolder.getUserId();
        List<CUserThirdPartyBO> boList = cUserService.getThirdPartyBindings(userId);
        return Result.success(CUserConverter.INSTANCE.toThirdPartyResponseList(boList));
    }

    /**
     * 解绑第三方
     */
    @PostMapping("/third-party/unbind")
    public Result<Void> unbindThirdParty(@Valid @RequestBody UnbindThirdPartyRequest request) {
        Long userId = UserContextHolder.getUserId();
        cUserService.unbindThirdParty(userId, request.getPlatform());
        return Result.success();
    }

    /**
     * 查看登录记录（近30天）
     */
    @GetMapping("/login-records")
    public Result<PageData<LoginLogResponse>> getLoginRecords(LoginLogPageRequest request) {
        Long userId = UserContextHolder.getUserId();
        LoginLogPageQueryBO queryBO = LoginLogConverter.INSTANCE.pageRequestToPageBo(request);
        Page<LoginLogBO> boPage = loginLogService.queryRecentLoginLogs(userId, UserTypeEnum.C.getCode(), queryBO);
        Page<LoginLogResponse> responsePage = LoginLogConverter.INSTANCE.toResponsePage(boPage);
        return Result.success(PageData.of(responsePage));
    }

    /**
     * 退出所有设备登录
     */
    @PostMapping("/logout-all")
    public Result<Void> logoutAll() {
        Long userId = UserContextHolder.getUserId();
        StpUtil.logout(userId);
        UserContextHolder.clear();
        return Result.success();
    }

    /**
     * 设置默认登录身份
     */
    @PutMapping("/identity-default")
    public Result<Void> setIdentityDefault(@Valid @RequestBody SetIdentityDefaultRequest request) {
        Long userId = UserContextHolder.getUserId();
        cUserService.setIdentityDefault(userId, request.getIdentityDefault());
        return Result.success();
    }
}

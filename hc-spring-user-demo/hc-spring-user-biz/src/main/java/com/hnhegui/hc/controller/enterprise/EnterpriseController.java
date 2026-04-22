package com.hnhegui.hc.controller.enterprise;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hc.framework.mybatis.model.PageData;
import com.hc.framework.web.model.Result;
import com.hnhegui.hc.bo.enterprise.EnterpriseBO;
import com.hnhegui.hc.bo.enterprise.EnterpriseCreateBO;
import com.hnhegui.hc.bo.enterprise.EnterpriseUserBO;
import com.hnhegui.hc.bo.enterprise.EnterpriseUserCreateBO;
import com.hnhegui.hc.bo.enterprise.EnterpriseUserPageQueryBO;
import com.hnhegui.hc.context.core.UserContextHolder;
import com.hnhegui.hc.controller.enterprise.converter.EnterpriseConverter;
import com.hnhegui.hc.controller.enterprise.request.EnterpriseCreateRequest;
import com.hnhegui.hc.controller.enterprise.request.EnterpriseUserCreateRequest;
import com.hnhegui.hc.controller.enterprise.request.EnterpriseUserPageRequest;
import com.hnhegui.hc.controller.enterprise.request.EnterpriseUpdateRequest;
import com.hnhegui.hc.controller.enterprise.request.ResetEnterpriseUserPasswordRequest;
import com.hnhegui.hc.controller.enterprise.request.SecuritySettingRequest;
import com.hnhegui.hc.controller.enterprise.response.EnterpriseResponse;
import com.hnhegui.hc.controller.enterprise.response.EnterpriseUserResponse;
import com.hnhegui.hc.service.enterprise.EnterpriseService;
import com.hnhegui.hc.service.enterprise.EnterpriseUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/enterprise")
@RequiredArgsConstructor
public class EnterpriseController {

    private final EnterpriseService enterpriseService;
    private final EnterpriseUserService enterpriseUserService;

    /**
     * 创建企业
     */
    @PostMapping("/add")
    public Result<EnterpriseResponse> createEnterprise(@Valid @RequestBody EnterpriseCreateRequest request) {
        EnterpriseCreateBO createBO = EnterpriseConverter.INSTANCE.createRequestToCreateBo(request);
        EnterpriseBO bo = enterpriseService.createEnterprise(createBO);
        return Result.success(EnterpriseConverter.INSTANCE.toResponse(bo));
    }

    /**
     * 获取企业信息
     */
    @GetMapping("/get/{id}")
    public Result<EnterpriseResponse> getEnterprise(@PathVariable Long id) {
        EnterpriseBO bo = enterpriseService.getEnterpriseById(id);
        return Result.success(EnterpriseConverter.INSTANCE.toResponse(bo));
    }

    /**
     * 修改企业信息
     */
    @PutMapping("/edit/{id}")
    public Result<EnterpriseResponse> updateEnterprise(@PathVariable Long id,
                                                       @Valid @RequestBody EnterpriseUpdateRequest request) {
        EnterpriseCreateBO createBO = EnterpriseConverter.INSTANCE.updateRequestToCreateBo(request);
        EnterpriseBO bo = enterpriseService.updateEnterprise(id, createBO);
        return Result.success(EnterpriseConverter.INSTANCE.toResponse(bo));
    }

    /**
     * 更新安全设置
     */
    @PutMapping("/security/{id}")
    public Result<Void> updateSecuritySettings(@PathVariable Long id,
                                               @Valid @RequestBody SecuritySettingRequest request) {
        enterpriseService.updateSecuritySettings(id, request.getIpWhitelist(),
                request.getLoginMutualExclusion(), request.getPasswordRule());
        return Result.success();
    }

    /**
     * 创建企业用户
     */
    @PostMapping("/user/add")
    public Result<EnterpriseUserResponse> createEnterpriseUser(@Valid @RequestBody EnterpriseUserCreateRequest request) {
        EnterpriseUserCreateBO createBO = EnterpriseConverter.INSTANCE.userCreateRequestToCreateBo(request);
        EnterpriseUserBO bo = enterpriseUserService.createEnterpriseUser(createBO);
        return Result.success(EnterpriseConverter.INSTANCE.toUserResponse(bo));
    }

    /**
     * 编辑企业用户
     */
    @PutMapping("/user/edit/{id}")
    public Result<EnterpriseUserResponse> updateEnterpriseUser(@PathVariable Long id,
                                                               @Valid @RequestBody EnterpriseUserCreateRequest request) {
        EnterpriseUserCreateBO createBO = EnterpriseConverter.INSTANCE.userCreateRequestToCreateBo(request);
        EnterpriseUserBO bo = enterpriseUserService.updateEnterpriseUser(id, createBO);
        return Result.success(EnterpriseConverter.INSTANCE.toUserResponse(bo));
    }

    /**
     * 软删除企业用户（已离职）
     */
    @DeleteMapping("/user/delete/{id}")
    public Result<Void> softDeleteEnterpriseUser(@PathVariable Long id) {
        enterpriseUserService.softDelete(id);
        return Result.success();
    }

    /**
     * 重置下属密码
     */
    @PutMapping("/user/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id,
                                      @Valid @RequestBody ResetEnterpriseUserPasswordRequest request) {
        enterpriseUserService.resetPassword(id, request.getNewPassword());
        return Result.success();
    }

    /**
     * 激活账号
     */
    @PostMapping("/user/{id}/activate")
    public Result<Void> activateUser(@PathVariable Long id) {
        enterpriseUserService.activateUser(id);
        return Result.success();
    }

    /**
     * 修改用户状态
     */
    @PutMapping("/user/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        Integer status = params.get("status");
        enterpriseUserService.updateStatus(id, status);
        return Result.success();
    }

    /**
     * 企业用户分页列表
     */
    @GetMapping("/user/page")
    public Result<PageData<EnterpriseUserResponse>> listEnterpriseUsers(EnterpriseUserPageRequest request) {
        EnterpriseUserPageQueryBO queryBO = EnterpriseConverter.INSTANCE.userPageRequestToPageBo(request);
        Page<EnterpriseUserBO> boPage = enterpriseUserService.listEnterpriseUsersByPage(queryBO);
        Page<EnterpriseUserResponse> responsePage = EnterpriseConverter.INSTANCE.toUserResponsePage(boPage);
        return Result.success(PageData.of(responsePage));
    }

    /**
     * B端用户首次登录强制修改密码
     */
    @PutMapping("/user/force-change-password")
    public Result<Void> forceChangePassword(@Valid @RequestBody ResetEnterpriseUserPasswordRequest request) {
        Long userId = UserContextHolder.getUserId();
        enterpriseUserService.forceChangePassword(userId, request.getNewPassword());
        return Result.success();
    }

    /**
     * B端用户修改密码
     */
    @PutMapping("/user/change-password")
    public Result<Void> changePassword(@Valid @RequestBody com.hnhegui.hc.controller.cuser.request.ChangePasswordRequest request) {
        Long userId = UserContextHolder.getUserId();
        enterpriseUserService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
        return Result.success();
    }
}

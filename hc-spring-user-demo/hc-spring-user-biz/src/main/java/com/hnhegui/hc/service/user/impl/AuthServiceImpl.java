package com.hnhegui.hc.service.user.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.hc.framework.satoken.util.SaTokenHelper;
import com.hc.framework.web.exception.BusinessException;
import com.hnhegui.hc.bo.auth.CUserLoginInfoBO;
import com.hnhegui.hc.config.RsaKeyConfig;
import com.hnhegui.hc.bo.auth.CurrentUserInfoBO;
import com.hnhegui.hc.bo.auth.EnterpriseUserLoginInfoBO;
import com.hnhegui.hc.bo.auth.IdentityItemBO;
import com.hnhegui.hc.bo.auth.LoginResultBO;
import com.hnhegui.hc.bo.cuser.CUserBO;
import com.hnhegui.hc.bo.enterprise.EnterpriseBO;
import com.hnhegui.hc.bo.enterprise.EnterpriseUserBO;
import com.hnhegui.hc.bo.user.UserBO;
import com.hnhegui.hc.common.enums.CUserStatusEnum;
import com.hnhegui.hc.common.enums.EnterpriseStatusEnum;
import com.hnhegui.hc.common.enums.EnterpriseUserStatusEnum;
import com.hnhegui.hc.common.enums.LoginStatusEnum;
import com.hnhegui.hc.common.enums.UserTypeEnum;
import com.hnhegui.hc.common.enums.VerificationCodeSceneEnum;
import com.hnhegui.hc.context.core.UserContext;
import com.hnhegui.hc.context.core.UserContextHolder;
import com.hnhegui.hc.service.cuser.CUserService;
import com.hnhegui.hc.service.enterprise.EnterpriseService;
import com.hnhegui.hc.service.enterprise.EnterpriseUserService;
import com.hnhegui.hc.service.log.LoginLogService;
import com.hnhegui.hc.service.user.AuthService;
import com.hnhegui.hc.service.user.UserService;
import com.hnhegui.hc.service.verify.AccountLockService;
import com.hnhegui.hc.service.verify.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.hnhegui.hc.common.constant.CommonConstant.USER_CONTEXT;

/**
 * 认证服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final CUserService cUserService;
    private final EnterpriseService enterpriseService;
    private final EnterpriseUserService enterpriseUserService;
    private final SaTokenHelper saTokenHelper;
    private final PasswordEncoder passwordEncoder;
    private final AccountLockService accountLockService;
    private final VerificationCodeService verificationCodeService;
    private final LoginLogService loginLogService;
    private final RsaKeyConfig rsaKeyConfig;

    // ====================== C端登录 ======================

    @Override
    public LoginResultBO cPasswordLogin(String account, String password, String loginIp, String device) {
        // 1. 检查账号是否被锁定
        if (accountLockService.isAccountLocked(UserTypeEnum.C.getCode(), account)) {
            long remaining = accountLockService.getAccountLockRemainingTime(UserTypeEnum.C.getCode(), account);
            loginLogService.recordLoginLog(UserTypeEnum.C.getCode(), null, account, loginIp, device, false, "账号已锁定");
            throw new BusinessException("账号已锁定，" + (remaining / 60) + "分钟后自动解锁");
        }

        // 2. 查询C端用户
        CUserBO cUser = cUserService.getCUserByAccount(account);
        if (cUser == null) {
            throw new BusinessException("账号或密码错误");
        }

        // 3. 检查账号状态
        checkCUserStatus(cUser, loginIp, device);

        // 4. 解密密码并校验
        String plainPassword = decryptPassword(password);
        if (!passwordEncoder.matches(plainPassword, cUser.getPassword())) {
            int errorCount = accountLockService.incrementPasswordErrorCount(UserTypeEnum.C.getCode(), cUser.getId(), account);
            loginLogService.recordLoginLog(UserTypeEnum.C.getCode(), cUser.getId(), account, loginIp, device, false, "密码错误");
            if (errorCount >= 5) {
                throw new BusinessException("密码错误次数过多，账号已锁定15分钟");
            }
            throw new BusinessException("账号或密码错误，还剩" + (5 - errorCount) + "次机会");
        }

        // 5. 密码正确，重置错误计数
        accountLockService.resetPasswordErrorCount(UserTypeEnum.C.getCode(), account);

        // 6. 执行登录
        saTokenHelper.login(cUser.getId());

        // 7. 构建用户上下文
        setUserContext(cUser, null);

        // 8. 记录登录日志
        loginLogService.recordLoginLog(UserTypeEnum.C.getCode(), cUser.getId(), account, loginIp, device, true, null);

        // 9. 返回结果
        LoginResultBO result = new LoginResultBO();
        result.setToken(saTokenHelper.getTokenValue());
        result.setTokenName(saTokenHelper.getTokenName());
        result.setUserType(UserTypeEnum.C.getCode());
        result.setCUserInfo(buildCUserLoginInfo(cUser));
        return result;
    }

    @Override
    public LoginResultBO cCodeLogin(String target, String code, String loginIp, String device) {
        // 1. 校验验证码
        if (!verificationCodeService.verifyCode(target, code, VerificationCodeSceneEnum.LOGIN.getCode())) {
            throw new BusinessException("验证码不正确或已过期");
        }

        // 2. 查询C端用户
        CUserBO cUser = cUserService.getCUserByAccount(target);
        if (cUser == null) {
            throw new BusinessException("该手机号/邮箱未注册C端账号");
        }

        // 3. 检查账号状态
        if (cUser.getStatus() == CUserStatusEnum.DISABLED.getCode()) {
            throw new BusinessException("账号已禁用，请联系客服");
        }
        if (cUser.getStatus() == CUserStatusEnum.LOCKED.getCode()) {
            throw new BusinessException("账号已锁定，请稍后再试");
        }

        // 4. 检查是否有多身份
        List<EnterpriseUserBO> bUsers = enterpriseUserService.getActiveUsersByPhone(target);
        if (!bUsers.isEmpty()) {
            return buildMultiIdentityResult(cUser, bUsers, target);
        }

        // 5. 仅C端身份，直接登录
        saTokenHelper.login(cUser.getId());
        setUserContext(cUser, null);
        loginLogService.recordLoginLog(UserTypeEnum.C.getCode(), cUser.getId(), target, loginIp, device, true, null);

        LoginResultBO result = new LoginResultBO();
        result.setToken(saTokenHelper.getTokenValue());
        result.setTokenName(saTokenHelper.getTokenName());
        result.setUserType(UserTypeEnum.C.getCode());
        result.setCUserInfo(buildCUserLoginInfo(cUser));
        return result;
    }

    // ====================== B端登录 ======================

    @Override
    public LoginResultBO bPasswordLogin(String enterpriseCode, String username, String password,
                                        String loginIp, String device) {
        // 1. 校验企业编码
        EnterpriseBO enterprise = enterpriseService.getEnterpriseByCode(enterpriseCode);
        if (enterprise == null) {
            throw new BusinessException("企业编码不存在");
        }
        if (enterprise.getStatus() != EnterpriseStatusEnum.NORMAL.getCode()) {
            throw new BusinessException("企业状态异常，无法登录");
        }

        // 2. IP白名单校验
        if (!enterpriseService.isIpAllowed(enterprise.getId(), loginIp)) {
            throw new BusinessException("当前IP不在企业允许的登录范围内");
        }

        // 3. 查询企业用户
        EnterpriseUserBO eUser = enterpriseUserService.getByEnterpriseIdAndUsername(enterprise.getId(), username);
        if (eUser == null) {
            loginLogService.recordLoginLog(UserTypeEnum.B.getCode(), null, username, loginIp, device, false, "账号不存在");
            throw new BusinessException("用户名或密码错误");
        }

        // 4. 检查账号是否被锁定
        if (accountLockService.isAccountLocked(UserTypeEnum.B.getCode(), username)) {
            long remaining = accountLockService.getAccountLockRemainingTime(UserTypeEnum.B.getCode(), username);
            loginLogService.recordLoginLog(UserTypeEnum.B.getCode(), eUser.getId(), username, loginIp, device, false, "账号已锁定");
            throw new BusinessException("账号已锁定，" + (remaining / 60) + "分钟后自动解锁");
        }

        // 5. 检查账号状态
        checkEnterpriseUserStatus(eUser);

        // 6. 检查激活是否过期
        enterpriseUserService.checkActivationExpire(eUser.getId());
        eUser = enterpriseUserService.getEnterpriseUserById(eUser.getId());

        // 7. 解密密码并校验
        String plainPassword = decryptPassword(password);
        if (!passwordEncoder.matches(plainPassword, eUser.getPassword())) {
            int errorCount = accountLockService.incrementPasswordErrorCount(UserTypeEnum.B.getCode(), eUser.getId(), username);
            loginLogService.recordLoginLog(UserTypeEnum.B.getCode(), eUser.getId(), username, loginIp, device, false, "密码错误");
            if (errorCount >= 5) {
                throw new BusinessException("密码错误次数过多，账号已锁定15分钟");
            }
            throw new BusinessException("用户名或密码错误，还剩" + (5 - errorCount) + "次机会");
        }

        // 8. 密码正确，重置错误计数
        accountLockService.resetPasswordErrorCount(UserTypeEnum.B.getCode(), username);

        // 9. 执行登录
        saTokenHelper.login(eUser.getId());

        // 10. 互踢策略检查
        if (enterpriseService.isLoginMutualExclusion(enterprise.getId())) {
            StpUtil.kickout(eUser.getId());
        }

        // 11. 构建用户上下文
        setBUserContext(eUser, enterprise);

        // 12. 记录登录日志
        loginLogService.recordLoginLog(UserTypeEnum.B.getCode(), eUser.getId(), username, loginIp, device, true, null);

        // 13. 返回结果
        LoginResultBO result = new LoginResultBO();
        result.setToken(saTokenHelper.getTokenValue());
        result.setTokenName(saTokenHelper.getTokenName());
        result.setUserType(UserTypeEnum.B.getCode());
        result.setEnterpriseId(enterprise.getId());
        result.setEnterpriseCode(enterprise.getEnterpriseCode());
        result.setEnterpriseName(enterprise.getName());
        result.setIsFirstLogin(eUser.getIsFirstLogin());
        result.setBUserInfo(buildEnterpriseUserLoginInfo(eUser, enterprise));
        return result;
    }

    @Override
    public LoginResultBO bCodeLogin(String target, String code, String enterpriseCode,
                                    String loginIp, String device) {
        // 1. 校验验证码
        if (!verificationCodeService.verifyCode(target, code, VerificationCodeSceneEnum.LOGIN.getCode())) {
            throw new BusinessException("验证码不正确或已过期");
        }

        // 2. 查询绑定该手机号/邮箱的企业用户
        List<EnterpriseUserBO> eUsers;
        if (target.contains("@")) {
            eUsers = enterpriseUserService.getActiveUsersByEmail(target);
        } else {
            eUsers = enterpriseUserService.getActiveUsersByPhone(target);
        }

        if (eUsers.isEmpty()) {
            throw new BusinessException("该手机号/邮箱未绑定任何企业账号");
        }

        // 3. 如果指定了企业编码，筛选对应企业
        if (enterpriseCode != null && !enterpriseCode.isEmpty()) {
            EnterpriseBO enterprise = enterpriseService.getEnterpriseByCode(enterpriseCode);
            if (enterprise == null) {
                throw new BusinessException("企业编码不存在");
            }
            Long entId = enterprise.getId();
            eUsers = eUsers.stream()
                .filter(u -> u.getEnterpriseId().equals(entId))
                .toList();
            if (eUsers.isEmpty()) {
                throw new BusinessException("该手机号/邮箱未绑定该企业账号");
            }
        }

        // 4. 如果只有一个企业用户，直接登录
        if (eUsers.size() == 1) {
            EnterpriseUserBO eUser = eUsers.get(0);
            EnterpriseBO enterprise = enterpriseService.getEnterpriseById(eUser.getEnterpriseId());

            // IP白名单校验
            if (!enterpriseService.isIpAllowed(enterprise.getId(), loginIp)) {
                throw new BusinessException("当前IP不在企业允许的登录范围内");
            }

            // 检查账号状态
            checkEnterpriseUserStatus(eUser);

            saTokenHelper.login(eUser.getId());
            setBUserContext(eUser, enterprise);
            loginLogService.recordLoginLog(UserTypeEnum.B.getCode(), eUser.getId(), target, loginIp, device, true, null);

            LoginResultBO result = new LoginResultBO();
            result.setToken(saTokenHelper.getTokenValue());
            result.setTokenName(saTokenHelper.getTokenName());
            result.setUserType(UserTypeEnum.B.getCode());
            result.setEnterpriseId(enterprise.getId());
            result.setEnterpriseCode(enterprise.getEnterpriseCode());
            result.setEnterpriseName(enterprise.getName());
            result.setIsFirstLogin(eUser.getIsFirstLogin());
            result.setBUserInfo(buildEnterpriseUserLoginInfo(eUser, enterprise));
            return result;
        }

        // 5. 多个企业，返回企业列表让用户选择（批量查询企业信息，避免循环内查询DB）
        List<Long> enterpriseIds = eUsers.stream()
            .map(EnterpriseUserBO::getEnterpriseId)
            .distinct()
            .toList();
        Map<Long, EnterpriseBO> enterpriseMap = enterpriseIds.stream()
            .collect(Collectors.toMap(
                id -> id,
                id -> enterpriseService.getEnterpriseById(id)
            ));

        List<IdentityItemBO> identityList = new ArrayList<>();
        for (EnterpriseUserBO eUser : eUsers) {
            EnterpriseBO ent = enterpriseMap.get(eUser.getEnterpriseId());
            if (ent != null) {
                IdentityItemBO item = new IdentityItemBO();
                item.setIdentityType(UserTypeEnum.B.getCode());
                item.setLabel(ent.getName());
                item.setEnterpriseId(ent.getId());
                item.setEnterpriseCode(ent.getEnterpriseCode());
                item.setUserId(eUser.getId());
                identityList.add(item);
            }
        }

        LoginResultBO result = new LoginResultBO();
        result.setNeedSelectIdentity(true);
        result.setPhone(target);
        result.setIdentityList(identityList);
        return result;
    }

    // ====================== 企业校验/身份选择 ======================

    @Override
    public EnterpriseBO checkEnterprise(String enterpriseCode) {
        EnterpriseBO enterprise = enterpriseService.getEnterpriseByCode(enterpriseCode);
        if (enterprise == null) {
            throw new BusinessException("企业编码不存在");
        }
        return enterprise;
    }

    @Override
    public LoginResultBO getIdentityList(String phone) {
        List<IdentityItemBO> identityList = new ArrayList<>();

        // C端身份
        CUserBO cUser = cUserService.getCUserByPhone(phone);
        if (cUser != null) {
            IdentityItemBO cIdentity = new IdentityItemBO();
            cIdentity.setIdentityType(UserTypeEnum.C.getCode());
            cIdentity.setLabel("个人中心");
            cIdentity.setUserId(cUser.getId());
            identityList.add(cIdentity);
        }

        // B端身份（批量查询企业信息，避免循环内查询DB）
        List<EnterpriseUserBO> bUsers = enterpriseUserService.getActiveUsersByPhone(phone);
        if (!bUsers.isEmpty()) {
            List<Long> enterpriseIds = bUsers.stream()
                .map(EnterpriseUserBO::getEnterpriseId)
                .distinct()
                .toList();
            Map<Long, EnterpriseBO> enterpriseMap = enterpriseIds.stream()
                .collect(Collectors.toMap(
                    id -> id,
                    id -> enterpriseService.getEnterpriseById(id)
                ));

            for (EnterpriseUserBO bUser : bUsers) {
                EnterpriseBO ent = enterpriseMap.get(bUser.getEnterpriseId());
                if (ent != null) {
                    IdentityItemBO bIdentity = new IdentityItemBO();
                    bIdentity.setIdentityType(UserTypeEnum.B.getCode());
                    bIdentity.setLabel(ent.getName());
                    bIdentity.setEnterpriseId(ent.getId());
                    bIdentity.setEnterpriseCode(ent.getEnterpriseCode());
                    bIdentity.setUserId(bUser.getId());
                    identityList.add(bIdentity);
                }
            }
        }

        LoginResultBO result = new LoginResultBO();
        result.setIdentityList(identityList);
        return result;
    }

    @Override
    public LoginResultBO selectIdentity(String phone, String identityType, Long enterpriseId,
                                        String loginIp, String device) {
        if (UserTypeEnum.C.getCode().equals(identityType)) {
            CUserBO cUser = cUserService.getCUserByPhone(phone);
            if (cUser == null) {
                throw new BusinessException("C端账号不存在");
            }

            saTokenHelper.login(cUser.getId());
            setUserContext(cUser, null);
            loginLogService.recordLoginLog(UserTypeEnum.C.getCode(), cUser.getId(), phone, loginIp, device, true, null);

            LoginResultBO result = new LoginResultBO();
            result.setToken(saTokenHelper.getTokenValue());
            result.setTokenName(saTokenHelper.getTokenName());
            result.setUserType(UserTypeEnum.C.getCode());
            result.setCUserInfo(buildCUserLoginInfo(cUser));
            return result;
        } else if (UserTypeEnum.B.getCode().equals(identityType)) {
            if (enterpriseId == null) {
                throw new BusinessException("B端身份必须指定企业ID");
            }
            List<EnterpriseUserBO> bUsers = enterpriseUserService.getActiveUsersByPhone(phone);
            EnterpriseUserBO eUser = bUsers.stream()
                .filter(u -> u.getEnterpriseId().equals(enterpriseId))
                .findFirst()
                .orElseThrow(() -> new BusinessException("未找到该企业下的账号"));

            EnterpriseBO enterprise = enterpriseService.getEnterpriseById(enterpriseId);
            saTokenHelper.login(eUser.getId());
            setBUserContext(eUser, enterprise);
            loginLogService.recordLoginLog(UserTypeEnum.B.getCode(), eUser.getId(), phone, loginIp, device, true, null);

            LoginResultBO result = new LoginResultBO();
            result.setToken(saTokenHelper.getTokenValue());
            result.setTokenName(saTokenHelper.getTokenName());
            result.setUserType(UserTypeEnum.B.getCode());
            result.setEnterpriseId(enterpriseId);
            result.setEnterpriseCode(enterprise.getEnterpriseCode());
            result.setEnterpriseName(enterprise.getName());
            result.setIsFirstLogin(eUser.getIsFirstLogin());
            result.setBUserInfo(buildEnterpriseUserLoginInfo(eUser, enterprise));
            return result;
        }

        throw new BusinessException("无效的身份类型");
    }

    // ====================== 平台管理员登录 ======================

    @Override
    public LoginResultBO platformLogin(String username, String password, String loginIp, String device) {
        // 1. 检查账号是否被锁定
        if (accountLockService.isAccountLocked(UserTypeEnum.P.getCode(), username)) {
            throw new BusinessException("账号已锁定，请稍后再试");
        }

        // 2. 查询平台用户
        UserBO userBO = userService.getUserByUsername(username);
        if (userBO == null) {
            loginLogService.recordLoginLog(UserTypeEnum.P.getCode(), null, username, loginIp, device, false, "账号不存在");
            throw new BusinessException("用户名或密码错误");
        }

        // 3. 解密密码并校验
        String plainPassword = decryptPassword(password);
        if (!passwordEncoder.matches(plainPassword, userBO.getPassword())) {
            int errorCount = accountLockService.incrementPasswordErrorCount(UserTypeEnum.P.getCode(), userBO.getId(), username);
            loginLogService.recordLoginLog(UserTypeEnum.P.getCode(), userBO.getId(), username, loginIp, device, false, "密码错误");
            if (errorCount >= 5) {
                throw new BusinessException("密码错误次数过多，账号已锁定15分钟");
            }
            throw new BusinessException("用户名或密码错误，还剩" + (5 - errorCount) + "次机会");
        }

        // 4. 密码正确，重置错误计数
        accountLockService.resetPasswordErrorCount(UserTypeEnum.P.getCode(), username);

        // 5. 执行登录
        saTokenHelper.login(userBO.getId());

        // 6. 构建用户上下文
        UserContext userContext = UserContext.builder()
            .userId(userBO.getId())
            .username(userBO.getUsername())
            .nickname(userBO.getName())
            .email(userBO.getEmail())
            .phone(userBO.getPhone())
            .userType(UserTypeEnum.P.getCode())
            .build();

        List<String> roleList = saTokenHelper.getRoleList();
        List<String> permissionList = saTokenHelper.getPermissionList();
        userContext.setRoles(roleList);
        userContext.setPermissions(permissionList);

        StpUtil.getSession().set(USER_CONTEXT, userContext);
        UserContextHolder.set(userContext);

        // 7. 记录登录日志
        loginLogService.recordLoginLog(UserTypeEnum.P.getCode(), userBO.getId(), username, loginIp, device, true, null);

        // 8. 返回结果
        LoginResultBO result = new LoginResultBO();
        result.setToken(saTokenHelper.getTokenValue());
        result.setTokenName(saTokenHelper.getTokenName());
        result.setUserType(UserTypeEnum.P.getCode());
        return result;
    }

    // ====================== 登出/用户信息 ======================

    @Override
    public void logout() {
        saTokenHelper.logout();
        UserContextHolder.clear();
    }

    @Override
    public CurrentUserInfoBO getCurrentUserInfo() {
        UserContext userContext = UserContextHolder.get();
        Long userId = UserContextHolder.getUserId();

        CurrentUserInfoBO info = new CurrentUserInfoBO();
        info.setUserType(userContext != null ? userContext.getUserType() : null);
        info.setRoles(saTokenHelper.getRoleList());
        info.setPermissions(saTokenHelper.getPermissionList());

        if (userContext != null && UserTypeEnum.C.getCode().equals(userContext.getUserType())) {
            CUserBO cUser = cUserService.getCUserById(userId);
            if (cUser != null) {
                info.setCUserInfo(buildCUserLoginInfo(cUser));
            }
        } else if (userContext != null && UserTypeEnum.B.getCode().equals(userContext.getUserType())) {
            EnterpriseUserBO eUser = enterpriseUserService.getEnterpriseUserById(userId);
            if (eUser != null) {
                EnterpriseBO ent = enterpriseService.getEnterpriseById(eUser.getEnterpriseId());
                info.setEnterpriseId(eUser.getEnterpriseId());
                info.setBUserInfo(buildEnterpriseUserLoginInfo(eUser, ent));
            }
        }

        return info;
    }

    // ====================== 辅助方法 ======================

    /**
     * 设置C端用户上下文
     */
    private void setUserContext(CUserBO cUser, EnterpriseBO enterprise) {
        UserContext.UserContextBuilder builder = UserContext.builder()
            .userId(cUser.getId())
            .username(cUser.getUsername())
            .nickname(cUser.getNickname())
            .email(cUser.getEmail())
            .phone(cUser.getPhone())
            .userType(UserTypeEnum.C.getCode());

        UserContext userContext = builder.build();
        StpUtil.getSession().set(USER_CONTEXT, userContext);
        UserContextHolder.set(userContext);
    }

    /**
     * 设置B端用户上下文
     */
    private void setBUserContext(EnterpriseUserBO eUser, EnterpriseBO enterprise) {
        UserContext.UserContextBuilder builder = UserContext.builder()
            .userId(eUser.getId())
            .username(eUser.getUsername())
            .nickname(eUser.getName())
            .email(eUser.getEmail())
            .phone(eUser.getPhone())
            .userType(UserTypeEnum.B.getCode())
            .enterpriseId(enterprise != null ? enterprise.getId() : null)
            .enterpriseCode(enterprise != null ? enterprise.getEnterpriseCode() : null);

        UserContext userContext = builder.build();

        List<String> roleList = saTokenHelper.getRoleList();
        List<String> permissionList = saTokenHelper.getPermissionList();
        userContext.setRoles(roleList);
        userContext.setPermissions(permissionList);

        StpUtil.getSession().set(USER_CONTEXT, userContext);
        UserContextHolder.set(userContext);
    }

    /**
     * 构建C端用户登录信息
     */
    private CUserLoginInfoBO buildCUserLoginInfo(CUserBO cUser) {
        CUserLoginInfoBO info = new CUserLoginInfoBO();
        info.setId(cUser.getId());
        info.setPhone(cUser.getPhone());
        info.setEmail(cUser.getEmail());
        info.setUsername(cUser.getUsername());
        info.setNickname(cUser.getNickname());
        info.setAvatar(cUser.getAvatar());
        info.setGender(cUser.getGender());
        info.setStatus(cUser.getStatus());
        return info;
    }

    /**
     * 构建B端企业用户登录信息
     */
    private EnterpriseUserLoginInfoBO buildEnterpriseUserLoginInfo(EnterpriseUserBO eUser, EnterpriseBO enterprise) {
        EnterpriseUserLoginInfoBO info = new EnterpriseUserLoginInfoBO();
        info.setId(eUser.getId());
        info.setEnterpriseId(eUser.getEnterpriseId());
        info.setUsername(eUser.getUsername());
        info.setName(eUser.getName());
        info.setPhone(eUser.getPhone());
        info.setEmail(eUser.getEmail());
        info.setStatus(eUser.getStatus());
        info.setIsFirstLogin(eUser.getIsFirstLogin());
        if (enterprise != null) {
            info.setEnterpriseName(enterprise.getName());
            info.setEnterpriseCode(enterprise.getEnterpriseCode());
        }
        return info;
    }

    /**
     * 构建多身份选择结果
     */
    private LoginResultBO buildMultiIdentityResult(CUserBO cUser, List<EnterpriseUserBO> bUsers, String phone) {
        // 批量查询企业信息，避免循环内查询DB
        List<Long> enterpriseIds = bUsers.stream()
            .map(EnterpriseUserBO::getEnterpriseId)
            .distinct()
            .toList();
        Map<Long, EnterpriseBO> enterpriseMap = enterpriseIds.stream()
            .collect(Collectors.toMap(
                id -> id,
                id -> enterpriseService.getEnterpriseById(id)
            ));

        List<IdentityItemBO> identityList = new ArrayList<>();

        // C端个人身份
        IdentityItemBO cIdentity = new IdentityItemBO();
        cIdentity.setIdentityType(UserTypeEnum.C.getCode());
        cIdentity.setLabel("个人中心");
        cIdentity.setUserId(cUser.getId());
        identityList.add(cIdentity);

        // B端企业身份
        for (EnterpriseUserBO bUser : bUsers) {
            EnterpriseBO ent = enterpriseMap.get(bUser.getEnterpriseId());
            if (ent != null) {
                IdentityItemBO bIdentity = new IdentityItemBO();
                bIdentity.setIdentityType(UserTypeEnum.B.getCode());
                bIdentity.setLabel(ent.getName());
                bIdentity.setEnterpriseId(ent.getId());
                bIdentity.setEnterpriseCode(ent.getEnterpriseCode());
                bIdentity.setUserId(bUser.getId());
                identityList.add(bIdentity);
            }
        }

        LoginResultBO result = new LoginResultBO();
        result.setNeedSelectIdentity(true);
        result.setPhone(phone);
        result.setIdentityList(identityList);
        if (cUser.getIdentityDefault() != null) {
            result.setIdentityDefault(cUser.getIdentityDefault());
        }
        return result;
    }

    /**
     * 检查C端用户状态
     */
    private void checkCUserStatus(CUserBO cUser, String loginIp, String device) {
        if (cUser.getStatus() == CUserStatusEnum.DISABLED.getCode()) {
            loginLogService.recordLoginLog(UserTypeEnum.C.getCode(), cUser.getId(), cUser.getPhone(), loginIp, device, false, "账号已禁用");
            throw new RuntimeException("账号已禁用，请联系客服");
        }
        if (cUser.getStatus() == CUserStatusEnum.LOCKED.getCode()) {
            loginLogService.recordLoginLog(UserTypeEnum.C.getCode(), cUser.getId(), cUser.getPhone(), loginIp, device, false, "账号已锁定");
            throw new RuntimeException("账号已锁定，请稍后再试");
        }
    }

    /**
     * RSA解密密码
     *
     * @param encryptedPassword 前端传来的RSA加密密文
     * @return 解密后的明文密码
     */
    private String decryptPassword(String encryptedPassword) {
        try {
            return rsaKeyConfig.decrypt(encryptedPassword);
        } catch (Exception e) {
            log.error("密码解密失败", e);
            throw new BusinessException("密码格式错误");
        }
    }

    /**
     * 检查B端企业用户状态
     */
    private void checkEnterpriseUserStatus(EnterpriseUserBO eUser) {
        if (eUser.getStatus() == EnterpriseUserStatusEnum.DISABLED.getCode()) {
            throw new RuntimeException("账号已禁用，请联系管理员");
        }
        if (eUser.getStatus() == EnterpriseUserStatusEnum.LOCKED.getCode()) {
            throw new RuntimeException("账号已锁定，请稍后再试");
        }
        if (eUser.getStatus() == EnterpriseUserStatusEnum.INACTIVE.getCode()) {
            throw new RuntimeException("账号未激活，请联系管理员激活");
        }
        if (eUser.getStatus() == EnterpriseUserStatusEnum.RESIGNED.getCode()) {
            throw new RuntimeException("账号已离职，无法登录");
        }
    }
}

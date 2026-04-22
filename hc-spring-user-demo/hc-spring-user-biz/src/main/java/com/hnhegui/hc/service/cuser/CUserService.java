package com.hnhegui.hc.service.cuser;

import com.hc.framework.web.exception.BusinessException;
import com.hnhegui.hc.bo.cuser.CUserBO;
import com.hnhegui.hc.bo.cuser.CUserThirdPartyBO;
import com.hnhegui.hc.common.enums.CUserStatusEnum;
import com.hnhegui.hc.common.enums.LockReasonEnum;
import com.hnhegui.hc.common.enums.UserTypeEnum;
import com.hnhegui.hc.common.enums.VerificationCodeSceneEnum;
import com.hnhegui.hc.controller.cuser.converter.CUserConverter;
import com.hnhegui.hc.entity.cuser.CUser;
import com.hnhegui.hc.entity.cuser.CUserThirdParty;
import com.hnhegui.hc.mapper.cuser.CUserMapper;
import com.hnhegui.hc.mapper.cuser.CUserThirdPartyMapper;
import com.hnhegui.hc.service.auth.PasswordService;
import com.hnhegui.hc.service.verify.AccountLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CUserService {

    private final CUserMapper cUserMapper;
    private final CUserThirdPartyMapper cUserThirdPartyMapper;
    private final PasswordEncoder passwordEncoder;
    private final PasswordService passwordService;
    private final AccountLockService accountLockService;

    // ====================== 查询 ======================

    /**
     * 根据ID查询C端用户
     *
     * @param id 用户ID
     * @return C端用户BO
     */
    public CUserBO getCUserById(Long id) {
        CUser entity = cUserMapper.selectById(id);
        return CUserConverter.INSTANCE.entityToBo(entity);
    }

    /**
     * 根据手机号查询C端用户
     *
     * @param phone 手机号
     * @return C端用户BO
     */
    public CUserBO getCUserByPhone(String phone) {
        CUser entity = cUserMapper.selectByPhone(phone);
        return CUserConverter.INSTANCE.entityToBo(entity);
    }

    /**
     * 根据邮箱查询C端用户
     *
     * @param email 邮箱
     * @return C端用户BO
     */
    public CUserBO getCUserByEmail(String email) {
        CUser entity = cUserMapper.selectByEmail(email);
        return CUserConverter.INSTANCE.entityToBo(entity);
    }

    /**
     * 根据用户名查询C端用户
     *
     * @param username 用户名
     * @return C端用户BO
     */
    public CUserBO getCUserByUsername(String username) {
        CUser entity = cUserMapper.selectByUsername(username);
        return CUserConverter.INSTANCE.entityToBo(entity);
    }

    /**
     * 根据账号（手机号/邮箱/用户名）查询C端用户（用于登录）
     *
     * @param account 账号
     * @return C端用户BO
     */
    public CUserBO getCUserByAccount(String account) {
        CUser entity = cUserMapper.selectByAccount(account);
        return CUserConverter.INSTANCE.entityToBo(entity);
    }

    /**
     * 获取第三方绑定列表
     *
     * @param userId 用户ID
     * @return 第三方绑定BO列表
     */
    public List<CUserThirdPartyBO> getThirdPartyBindings(Long userId) {
        List<CUserThirdParty> list = cUserThirdPartyMapper.selectByUserId(userId);
        return CUserConverter.INSTANCE.thirdPartyEntityToBoList(list);
    }

    // ====================== 注册 ======================

    /**
     * C端用户注册
     *
     * @param phone    手机号
     * @param password 密码
     * @param email    邮箱
     * @param username 用户名
     * @return C端用户BO
     */
    public CUserBO register(String phone, String password, String email, String username) {
        // 校验密码复杂度
        String validateMsg = passwordService.validatePassword(password);
        if (validateMsg != null) {
            throw new BusinessException(validateMsg);
        }

        // 校验手机号是否已注册
        if (phone != null && cUserMapper.selectByPhone(phone) != null) {
            throw new RuntimeException("该手机号已注册");
        }

        // 校验邮箱是否已注册
        if (email != null && cUserMapper.selectByEmail(email) != null) {
            throw new RuntimeException("该邮箱已注册");
        }

        // 校验用户名是否已存在
        if (username != null && cUserMapper.selectByUsername(username) != null) {
            throw new RuntimeException("该用户名已存在");
        }

        CUser cUser = new CUser();
        cUser.setPhone(phone);
        cUser.setEmail(email);
        cUser.setUsername(username);
        cUser.setPassword(passwordEncoder.encode(password));
        cUser.setStatus(CUserStatusEnum.NORMAL.getCode());
        cUser.setPasswordErrorCount(0);
        cUser.setRememberLogin(0);
        cUserMapper.insert(cUser);

        log.info("C端用户注册成功：phone={}", phone);
        return CUserConverter.INSTANCE.entityToBo(cUser);
    }

    // ====================== 密码操作 ======================

    /**
     * 找回密码（通过验证码验证后）
     *
     * @param account     账号
     * @param newPassword 新密码
     */
    public void resetPassword(String account, String newPassword) {
        String validateMsg = passwordService.validatePassword(newPassword);
        if (validateMsg != null) {
            throw new RuntimeException(validateMsg);
        }

        CUser cUser = cUserMapper.selectByAccount(account);
        if (cUser == null) {
            throw new RuntimeException("账号不存在");
        }

        if (passwordService.isSameAsOldPassword(newPassword, cUser.getPassword(), passwordEncoder)) {
            throw new RuntimeException("新密码不能与原密码相同");
        }

        cUser.setPassword(passwordEncoder.encode(newPassword));
        cUserMapper.updateById(cUser);

        // 重置密码错误计数并解锁
        accountLockService.unlockAccount(UserTypeEnum.C.getCode(), account);

        log.info("C端用户密码重置成功：account={}", account);
    }

    /**
     * 修改密码
     *
     * @param userId      用户ID
     * @param oldPassword 原密码
     * @param newPassword 新密码
     */
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        String validateMsg = passwordService.validatePassword(newPassword);
        if (validateMsg != null) {
            throw new RuntimeException(validateMsg);
        }

        CUser cUser = cUserMapper.selectById(userId);
        if (cUser == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!passwordEncoder.matches(oldPassword, cUser.getPassword())) {
            throw new RuntimeException("原密码错误");
        }

        if (passwordService.isSameAsOldPassword(newPassword, cUser.getPassword(), passwordEncoder)) {
            throw new RuntimeException("新密码不能与原密码相同");
        }

        cUser.setPassword(passwordEncoder.encode(newPassword));
        cUserMapper.updateById(cUser);

        log.info("C端用户修改密码成功：userId={}", userId);
    }

    // ====================== 个人信息 ======================

    /**
     * 绑定/更换手机号
     *
     * @param userId   用户ID
     * @param newPhone 新手机号
     */
    public void changePhone(Long userId, String newPhone) {
        CUser existingUser = cUserMapper.selectByPhone(newPhone);
        if (existingUser != null && !existingUser.getId().equals(userId)) {
            throw new RuntimeException("该手机号已被其他账号绑定");
        }

        CUser cUser = cUserMapper.selectById(userId);
        if (cUser == null) {
            throw new RuntimeException("用户不存在");
        }

        cUser.setPhone(newPhone);
        cUserMapper.updateById(cUser);

        log.info("C端用户更换手机号成功：userId={}", userId);
    }

    /**
     * 绑定/更换邮箱
     *
     * @param userId   用户ID
     * @param newEmail 新邮箱
     */
    public void changeEmail(Long userId, String newEmail) {
        CUser existingUser = cUserMapper.selectByEmail(newEmail);
        if (existingUser != null && !existingUser.getId().equals(userId)) {
            throw new RuntimeException("该邮箱已被其他账号绑定");
        }

        CUser cUser = cUserMapper.selectById(userId);
        if (cUser == null) {
            throw new RuntimeException("用户不存在");
        }

        cUser.setEmail(newEmail);
        cUserMapper.updateById(cUser);

        log.info("C端用户更换邮箱成功：userId={}", userId);
    }

    /**
     * 更新个人信息
     *
     * @param userId   用户ID
     * @param nickname 昵称
     * @param avatar   头像
     * @param gender   性别
     * @param birthday 生日
     * @return C端用户BO
     */
    public CUserBO updateProfile(Long userId, String nickname, String avatar, Integer gender, java.time.LocalDate birthday) {
        CUser cUser = cUserMapper.selectById(userId);
        if (cUser == null) {
            throw new RuntimeException("用户不存在");
        }

        if (nickname != null) {
            cUser.setNickname(nickname);
        }
        if (avatar != null) {
            cUser.setAvatar(avatar);
        }
        if (gender != null) {
            cUser.setGender(gender);
        }
        if (birthday != null) {
            cUser.setBirthday(birthday);
        }
        cUserMapper.updateById(cUser);

        return CUserConverter.INSTANCE.entityToBo(cUser);
    }

    // ====================== 第三方绑定 ======================

    /**
     * 解绑第三方账号
     *
     * @param userId   用户ID
     * @param platform 平台
     */
    public void unbindThirdParty(Long userId, String platform) {
        cUserThirdPartyMapper.deleteByUserIdAndPlatform(userId, platform);

        log.info("C端用户解绑第三方：userId={}, platform={}", userId, platform);
    }

    /**
     * 绑定第三方账号
     *
     * @param userId       用户ID
     * @param platform     平台
     * @param openId       openId
     * @param unionId      unionId
     * @param bindNickname 昵称
     * @param bindAvatar   头像
     */
    public void bindThirdParty(Long userId, String platform, String openId, String unionId,
                               String bindNickname, String bindAvatar) {
        CUserThirdParty existing = cUserThirdPartyMapper.selectByPlatformAndOpenId(platform, openId);
        if (existing != null && !existing.getUserId().equals(userId)) {
            throw new RuntimeException("该第三方账号已绑定其他账号，请先解绑后再重新绑定");
        }

        CUserThirdParty binding = new CUserThirdParty();
        binding.setUserId(userId);
        binding.setPlatform(platform);
        binding.setOpenId(openId);
        binding.setUnionId(unionId);
        binding.setBindNickname(bindNickname);
        binding.setBindAvatar(bindAvatar);
        cUserThirdPartyMapper.insert(binding);

        log.info("C端用户绑定第三方：userId={}, platform={}", userId, platform);
    }

    /**
     * 根据第三方openId查询绑定的C端用户
     *
     * @param platform 平台
     * @param openId   openId
     * @return C端用户BO
     */
    public CUserBO getCUserByThirdPartyOpenId(String platform, String openId) {
        CUserThirdParty binding = cUserThirdPartyMapper.selectByPlatformAndOpenId(platform, openId);
        if (binding == null) {
            return null;
        }
        CUser cUser = cUserMapper.selectById(binding.getUserId());
        return CUserConverter.INSTANCE.entityToBo(cUser);
    }

    // ====================== 其他操作 ======================

    /**
     * 设置默认登录身份
     *
     * @param userId          用户ID
     * @param identityDefault 默认身份
     */
    public void setIdentityDefault(Long userId, String identityDefault) {
        CUser cUser = cUserMapper.selectById(userId);
        if (cUser == null) {
            throw new RuntimeException("用户不存在");
        }
        cUser.setIdentityDefault(identityDefault);
        cUserMapper.updateById(cUser);
    }

    /**
     * 更新登录状态（锁定/解锁/禁用）
     *
     * @param userId 用户ID
     * @param status 状态
     */
    public void updateStatus(Long userId, int status) {
        CUser cUser = cUserMapper.selectById(userId);
        if (cUser == null) {
            throw new RuntimeException("用户不存在");
        }
        cUser.setStatus(status);
        if (status == CUserStatusEnum.LOCKED.getCode()) {
            cUser.setLockTime(LocalDateTime.now());
        } else if (status == CUserStatusEnum.NORMAL.getCode()) {
            cUser.setLockTime(null);
            cUser.setPasswordErrorCount(0);
        }
        cUserMapper.updateById(cUser);
    }

    /**
     * 批量插入
     *
     * @param list 实体集合
     * @return 影响行数
     */
    public int insertBatch(List<CUser> list) {
        return cUserMapper.insertBatch(list);
    }

    /**
     * 批量插入或更新
     *
     * @param list 实体集合
     * @return 影响行数
     */
    public int insertOrUpdateBatch(List<CUser> list) {
        return cUserMapper.insertOrUpdateBatch(list);
    }
}

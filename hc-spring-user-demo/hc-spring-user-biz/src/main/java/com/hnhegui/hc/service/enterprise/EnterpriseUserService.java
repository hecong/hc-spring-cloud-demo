package com.hnhegui.hc.service.enterprise;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnhegui.hc.bo.enterprise.EnterpriseUserBO;
import com.hnhegui.hc.bo.enterprise.EnterpriseUserCreateBO;
import com.hnhegui.hc.bo.enterprise.EnterpriseUserPageQueryBO;
import com.hnhegui.hc.common.enums.EnterpriseUserStatusEnum;
import com.hnhegui.hc.common.enums.LockReasonEnum;
import com.hnhegui.hc.common.enums.UserTypeEnum;
import com.hnhegui.hc.controller.enterprise.converter.EnterpriseConverter;
import com.hnhegui.hc.entity.enterprise.EnterpriseUser;
import com.hnhegui.hc.mapper.enterprise.EnterpriseUserMapper;
import com.hnhegui.hc.service.auth.PasswordService;
import com.hnhegui.hc.service.verify.AccountLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnterpriseUserService {

    private final EnterpriseUserMapper enterpriseUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final PasswordService passwordService;
    private final AccountLockService accountLockService;

    /**
     * B端用户状态枚举引用（不再定义常量）
     */

    /**
     * 激活有效期（天）
     */
    private static final int ACTIVATION_EXPIRE_DAYS = 7;

    // ====================== 查询 ======================

    /**
     * 根据ID查询企业用户
     *
     * @param id 用户ID
     * @return 企业用户BO
     */
    public EnterpriseUserBO getEnterpriseUserById(Long id) {
        EnterpriseUser entity = enterpriseUserMapper.selectById(id);
        return EnterpriseConverter.INSTANCE.userEntityToBo(entity);
    }

    /**
     * 根据企业ID和用户名查询
     *
     * @param enterpriseId 企业ID
     * @param username     用户名
     * @return 企业用户BO
     */
    public EnterpriseUserBO getByEnterpriseIdAndUsername(Long enterpriseId, String username) {
        EnterpriseUser entity = enterpriseUserMapper.selectByEnterpriseIdAndUsername(enterpriseId, username);
        return EnterpriseConverter.INSTANCE.userEntityToBo(entity);
    }

    /**
     * 根据手机号查询在职企业用户列表
     *
     * @param phone 手机号
     * @return 企业用户BO列表
     */
    public List<EnterpriseUserBO> getActiveUsersByPhone(String phone) {
        List<EnterpriseUser> list = enterpriseUserMapper.selectActiveByPhone(phone);
        return EnterpriseConverter.INSTANCE.userEntityToBoList(list);
    }

    /**
     * 根据邮箱查询在职企业用户列表
     *
     * @param email 邮箱
     * @return 企业用户BO列表
     */
    public List<EnterpriseUserBO> getActiveUsersByEmail(String email) {
        List<EnterpriseUser> list = enterpriseUserMapper.selectActiveByEmail(email);
        return EnterpriseConverter.INSTANCE.userEntityToBoList(list);
    }

    /**
     * 分页查询企业用户列表
     *
     * @param queryBO 查询参数
     * @return 分页结果
     */
    public Page<EnterpriseUserBO> listEnterpriseUsersByPage(EnterpriseUserPageQueryBO queryBO) {
        Page<EnterpriseUser> entityPage = enterpriseUserMapper.selectEnterpriseUsersByPage(queryBO);
        return EnterpriseConverter.INSTANCE.userEntityPageToBoPage(entityPage);
    }

    /**
     * 批量查询企业用户（根据企业ID列表），返回 enterpriseId -> List<EnterpriseUserBO> 的映射
     *
     * @param enterpriseIds 企业ID列表
     * @return 企业ID -> 企业用户BO列表
     */
    public Map<Long, List<EnterpriseUserBO>> getUsersByEnterpriseIds(List<Long> enterpriseIds) {
        List<EnterpriseUser> users = enterpriseUserMapper.selectBatchIds(enterpriseIds);
        List<EnterpriseUserBO> boList = EnterpriseConverter.INSTANCE.userEntityToBoList(users);
        return boList.stream().collect(Collectors.groupingBy(EnterpriseUserBO::getEnterpriseId));
    }

    // ====================== 保存 ======================

    /**
     * 创建企业用户
     *
     * @param createBO 创建参数
     * @return 企业用户BO
     */
    public EnterpriseUserBO createEnterpriseUser(EnterpriseUserCreateBO createBO) {
        // 校验用户名唯一性（同企业下）
        if (enterpriseUserMapper.selectByEnterpriseIdAndUsername(
                createBO.getEnterpriseId(), createBO.getUsername()) != null) {
            throw new RuntimeException("该企业下用户名已存在");
        }

        // 校验密码复杂度
        String validateMsg = passwordService.validatePassword(createBO.getPassword());
        if (validateMsg != null) {
            throw new RuntimeException(validateMsg);
        }

        EnterpriseUser user = EnterpriseConverter.INSTANCE.userCreateBoToEntity(createBO);
        user.setPassword(passwordEncoder.encode(createBO.getPassword()));
        user.setStatus(EnterpriseUserStatusEnum.INACTIVE.getCode());
        user.setPasswordErrorCount(0);
        user.setIsFirstLogin(1);
        user.setActivationExpireTime(LocalDateTime.now().plusDays(ACTIVATION_EXPIRE_DAYS));
        enterpriseUserMapper.insert(user);

        log.info("B端企业用户创建成功：enterpriseId={}, username={}", createBO.getEnterpriseId(), createBO.getUsername());
        return EnterpriseConverter.INSTANCE.userEntityToBo(user);
    }

    // ====================== 更新 ======================

    /**
     * 更新企业用户信息
     *
     * @param id       用户ID
     * @param createBO 更新参数
     * @return 企业用户BO
     */
    public EnterpriseUserBO updateEnterpriseUser(Long id, EnterpriseUserCreateBO createBO) {
        EnterpriseUser user = enterpriseUserMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (createBO.getName() != null) {
            user.setName(createBO.getName());
        }
        if (createBO.getPhone() != null) {
            user.setPhone(createBO.getPhone());
        }
        if (createBO.getEmail() != null) {
            user.setEmail(createBO.getEmail());
        }
        enterpriseUserMapper.updateById(user);

        log.info("B端企业用户更新成功：id={}", id);
        return EnterpriseConverter.INSTANCE.userEntityToBo(user);
    }

    // ====================== 密码操作 ======================

    /**
     * 重置密码（管理员重置下属密码）
     *
     * @param id          用户ID
     * @param newPassword 新密码
     */
    public void resetPassword(Long id, String newPassword) {
        String validateMsg = passwordService.validatePassword(newPassword);
        if (validateMsg != null) {
            throw new RuntimeException(validateMsg);
        }

        EnterpriseUser user = enterpriseUserMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setIsFirstLogin(1);
        enterpriseUserMapper.updateById(user);

        // 重置密码错误计数并解锁
        accountLockService.unlockAccount(UserTypeEnum.B.getCode(), user.getUsername());

        log.info("B端企业用户密码重置：id={}", id);
    }

    /**
     * 首次登录强制修改密码
     *
     * @param id          用户ID
     * @param newPassword 新密码
     */
    public void forceChangePassword(Long id, String newPassword) {
        String validateMsg = passwordService.validatePassword(newPassword);
        if (validateMsg != null) {
            throw new RuntimeException(validateMsg);
        }

        EnterpriseUser user = enterpriseUserMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setIsFirstLogin(0);
        enterpriseUserMapper.updateById(user);

        log.info("B端企业用户首次登录修改密码：id={}", id);
    }

    /**
     * 修改密码
     *
     * @param id          用户ID
     * @param oldPassword 原密码
     * @param newPassword 新密码
     */
    public void changePassword(Long id, String oldPassword, String newPassword) {
        String validateMsg = passwordService.validatePassword(newPassword);
        if (validateMsg != null) {
            throw new RuntimeException(validateMsg);
        }

        EnterpriseUser user = enterpriseUserMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }

        if (passwordService.isSameAsOldPassword(newPassword, user.getPassword(), passwordEncoder)) {
            throw new RuntimeException("新密码不能与原密码相同");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        enterpriseUserMapper.updateById(user);

        log.info("B端企业用户修改密码：id={}", id);
    }

    // ====================== 状态操作 ======================

    /**
     * 软删除（已离职），状态不可逆
     *
     * @param id 用户ID
     */
    public void softDelete(Long id) {
        EnterpriseUser user = enterpriseUserMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setStatus(EnterpriseUserStatusEnum.RESIGNED.getCode());
        enterpriseUserMapper.updateById(user);

        log.info("B端企业用户软删除（已离职）：id={}, username={}", id, user.getUsername());
    }

    /**
     * 激活账号
     *
     * @param id 用户ID
     */
    public void activateUser(Long id) {
        EnterpriseUser user = enterpriseUserMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (user.getStatus() != EnterpriseUserStatusEnum.INACTIVE.getCode() && user.getStatus() != EnterpriseUserStatusEnum.DISABLED.getCode()) {
            throw new RuntimeException("当前状态不允许激活");
        }

        user.setStatus(EnterpriseUserStatusEnum.NORMAL.getCode());
        user.setActivationExpireTime(LocalDateTime.now().plusDays(ACTIVATION_EXPIRE_DAYS));
        enterpriseUserMapper.updateById(user);

        log.info("B端企业用户激活成功：id={}", id);
    }

    /**
     * 更新用户状态
     *
     * @param id     用户ID
     * @param status 状态
     */
    public void updateStatus(Long id, int status) {
        EnterpriseUser user = enterpriseUserMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setStatus(status);
        if (status == EnterpriseUserStatusEnum.NORMAL.getCode()) {
            user.setLockTime(null);
            user.setPasswordErrorCount(0);
        } else if (status == EnterpriseUserStatusEnum.LOCKED.getCode()) {
            user.setLockTime(LocalDateTime.now());
        }
        enterpriseUserMapper.updateById(user);
    }

    /**
     * 检查激活是否过期，过期则自动变为禁用
     *
     * @param id 用户ID
     */
    public void checkActivationExpire(Long id) {
        EnterpriseUser user = enterpriseUserMapper.selectById(id);
        if (user == null) {
            return;
        }
        if (user.getStatus() == EnterpriseUserStatusEnum.INACTIVE.getCode()
                && user.getActivationExpireTime() != null
                && LocalDateTime.now().isAfter(user.getActivationExpireTime())) {
            user.setStatus(EnterpriseUserStatusEnum.DISABLED.getCode());
            enterpriseUserMapper.updateById(user);
            log.info("B端企业用户激活过期，自动禁用：id={}", id);
        }
    }

    // ====================== 批量操作 ======================

    /**
     * 批量插入
     *
     * @param list 实体集合
     * @return 影响行数
     */
    public int insertBatch(List<EnterpriseUser> list) {
        return enterpriseUserMapper.insertBatch(list);
    }

    /**
     * 批量插入或更新
     *
     * @param list 实体集合
     * @return 影响行数
     */
    public int insertOrUpdateBatch(List<EnterpriseUser> list) {
        return enterpriseUserMapper.insertOrUpdateBatch(list);
    }
}

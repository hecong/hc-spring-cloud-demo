package com.hnhegui.hc.service.verify;

import com.hnhegui.hc.common.enums.AccountLockStatusEnum;
import com.hnhegui.hc.common.enums.LockReasonEnum;
import com.hnhegui.hc.entity.verify.AccountLock;
import com.hnhegui.hc.mapper.verify.AccountLockMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountLockService {

    private final AccountLockMapper accountLockMapper;
    private final StringRedisTemplate redisTemplate;

    /**
     * 密码错误次数上限
     */
    private static final int MAX_PASSWORD_ERROR_COUNT = 5;

    /**
     * 锁定时长（分钟）
     */
    private static final int LOCK_DURATION_MINUTES = 15;

    // ====================== 密码错误计数 ======================

    /**
     * 增加密码错误次数
     *
     * @param userType 用户类型：C/B/P
     * @param userId   用户ID
     * @param account  账号
     * @return 当前错误次数
     */
    public int incrementPasswordErrorCount(String userType, Long userId, String account) {
        String key = "auth:pwd:error:" + userType + ":" + account;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            redisTemplate.expire(key, LOCK_DURATION_MINUTES, TimeUnit.MINUTES);
        }

        int errorCount = count != null ? count.intValue() : 1;

        // 达到阈值，锁定账号
        if (errorCount >= MAX_PASSWORD_ERROR_COUNT) {
            lockAccount(userType, userId, account, LockReasonEnum.PASSWORD_ERROR.getCode());
        }

        return errorCount;
    }

    /**
     * 重置密码错误次数
     *
     * @param userType 用户类型：C/B/P
     * @param account  账号
     */
    public void resetPasswordErrorCount(String userType, String account) {
        String key = "auth:pwd:error:" + userType + ":" + account;
        redisTemplate.delete(key);
    }

    // ====================== 账号锁定/解锁 ======================

    /**
     * 检查账号是否被锁定
     *
     * @param userType 用户类型：C/B/P
     * @param account  账号
     * @return 是否被锁定
     */
    public boolean isAccountLocked(String userType, String account) {
        String key = "auth:lock:" + userType + ":" + account;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 获取账号剩余锁定时间（秒）
     *
     * @param userType 用户类型：C/B/P
     * @param account  账号
     * @return 剩余锁定时间（秒），未锁定返回0
     */
    public long getAccountLockRemainingTime(String userType, String account) {
        String key = "auth:lock:" + userType + ":" + account;
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        return ttl != null && ttl > 0 ? ttl : 0;
    }

    /**
     * 锁定账号
     *
     * @param userType   用户类型：C/B/P
     * @param userId     用户ID
     * @param account    账号
     * @param lockReason 锁定原因
     */
    public void lockAccount(String userType, Long userId, String account, String lockReason) {
        // Redis 锁定标记
        String key = "auth:lock:" + userType + ":" + account;
        redisTemplate.opsForValue().set(key, String.valueOf(userId), LOCK_DURATION_MINUTES, TimeUnit.MINUTES);

        // 记录锁定日志
        AccountLock accountLock = new AccountLock();
        accountLock.setUserType(userType);
        accountLock.setUserId(userId);
        accountLock.setAccount(account);
        accountLock.setLockReason(lockReason);
        accountLock.setUnlockTime(LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES));
        accountLock.setStatus(AccountLockStatusEnum.LOCKED.getCode());
        accountLockMapper.insert(accountLock);

        log.info("账号已锁定：userType={}, account={}, reason={}", userType, account, lockReason);
    }

    /**
     * 解锁账号
     *
     * @param userType 用户类型：C/B/P
     * @param account  账号
     */
    public void unlockAccount(String userType, String account) {
        // 移除 Redis 锁定标记
        String lockKey = "auth:lock:" + userType + ":" + account;
        redisTemplate.delete(lockKey);

        // 重置密码错误计数
        resetPasswordErrorCount(userType, account);

        // 更新锁定记录状态（使用 Mapper 方法）
        accountLockMapper.unlockByUserTypeAndAccount(userType, account);

        log.info("账号已解锁：userType={}, account={}", userType, account);
    }

    /**
     * 检查并自动解锁已过期的锁定
     *
     * @param userType 用户类型：C/B/P
     * @param account  账号
     */
    public void checkAndAutoUnlock(String userType, String account) {
        if (!isAccountLocked(userType, account)) {
            long remainingTime = getAccountLockRemainingTime(userType, account);
            if (remainingTime <= 0) {
                unlockAccount(userType, account);
            }
        }
    }
}

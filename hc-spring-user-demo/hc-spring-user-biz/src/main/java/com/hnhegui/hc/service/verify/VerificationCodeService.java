package com.hnhegui.hc.service.verify;

import com.hnhegui.hc.common.enums.VerificationCodeSceneEnum;
import com.hnhegui.hc.common.enums.VerificationCodeStatusEnum;
import com.hnhegui.hc.entity.verify.VerificationCode;
import com.hnhegui.hc.mapper.verify.VerificationCodeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationCodeService {

    private final VerificationCodeMapper verificationCodeMapper;
    private final StringRedisTemplate redisTemplate;

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 验证码长度
     */
    private static final int CODE_LENGTH = 6;

    /**
     * 验证码有效时长（分钟）
     */
    private static final int CODE_EXPIRE_MINUTES = 5;

    /**
     * 同一手机号/邮箱 1小时上限次数
     */
    private static final int HOURLY_LIMIT = 5;

    /**
     * 同一手机号/邮箱 24小时上限次数
     */
    private static final int DAILY_LIMIT = 10;

    /**
     * 同一IP 1分钟上限次数
     */
    private static final int IP_MINUTE_LIMIT = 1;

    /**
     * 单日请求上限（超过加入黑名单）
     */
    private static final int BLACKLIST_THRESHOLD = 20;

    // ====================== 验证码生成/发送 ======================

    /**
     * 生成6位数字验证码
     */
    private String generateCode() {
        int code = RANDOM.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    /**
     * 发送验证码
     *
     * @param target 发送目标（手机号/邮箱）
     * @param scene  场景：login/register/reset
     * @param ip     请求IP
     * @return 验证码（生产环境中不会返回，由短信/邮件服务发送）
     */
    public String sendCode(String target, String scene, String ip) {
        // 1. 检查黑名单
        if (isInBlacklist(target)) {
            throw new RuntimeException("请求次数过多，请24小时后再试");
        }

        // 2. 检查IP频次限制（1分钟内同IP只能请求1次）
        String ipLimitKey = "auth:code:limit:ip:" + ip;
        Long ipCount = redisTemplate.opsForValue().increment(ipLimitKey);
        if (ipCount != null && ipCount == 1) {
            redisTemplate.expire(ipLimitKey, 1, TimeUnit.MINUTES);
        }
        if (ipCount != null && ipCount > IP_MINUTE_LIMIT) {
            throw new RuntimeException("请求过于频繁，请稍后再试");
        }

        // 3. 检查目标频次限制（1小时内5次，24小时内10次）
        String hourlyKey = "auth:code:limit:target:" + target + ":hourly";
        String dailyKey = "auth:code:limit:target:" + target + ":daily";

        Long hourlyCount = redisTemplate.opsForValue().increment(hourlyKey);
        if (hourlyCount != null && hourlyCount == 1) {
            redisTemplate.expire(hourlyKey, 1, TimeUnit.HOURS);
        }
        if (hourlyCount != null && hourlyCount > HOURLY_LIMIT) {
            throw new RuntimeException("1小时内发送次数已达上限");
        }

        Long dailyCount = redisTemplate.opsForValue().increment(dailyKey);
        if (dailyCount != null && dailyCount == 1) {
            redisTemplate.expire(dailyKey, 24, TimeUnit.HOURS);
        }
        if (dailyCount != null && dailyCount > DAILY_LIMIT) {
            throw new RuntimeException("今日发送次数已达上限");
        }

        // 4. 超过黑名单阈值，加入黑名单
        if (dailyCount != null && dailyCount >= BLACKLIST_THRESHOLD) {
            addToBlacklist(target);
            throw new RuntimeException("请求次数异常，账号已被限制24小时");
        }

        // 5. 使之前的验证码失效（使用 Mapper 方法）
        verificationCodeMapper.expireValidCodes(target, scene);

        // 6. 生成新验证码
        String code = generateCode();

        // 7. 存入Redis（用于快速校验）
        String redisKey = "auth:code:" + target + ":" + scene;
        redisTemplate.opsForValue().set(redisKey, code, CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);

        // 8. 记录到数据库
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setTarget(target);
        verificationCode.setCode(code);
        verificationCode.setScene(scene);
        verificationCode.setStatus(VerificationCodeStatusEnum.VALID.getCode());
        verificationCode.setRequestIp(ip);
        verificationCode.setExpireTime(LocalDateTime.now().plusMinutes(CODE_EXPIRE_MINUTES));
        verificationCodeMapper.insert(verificationCode);

        log.info("验证码已发送：target={}, scene={}", target, scene);

        // TODO: 调用短信/邮件服务发送验证码
        return code;
    }

    // ====================== 验证码校验 ======================

    /**
     * 校验验证码
     *
     * @param target 发送目标（手机号/邮箱）
     * @param code   验证码
     * @param scene  场景
     * @return 是否校验通过
     */
    public boolean verifyCode(String target, String code, String scene) {
        String redisKey = "auth:code:" + target + ":" + scene;
        String storedCode = redisTemplate.opsForValue().get(redisKey);

        if (storedCode == null || !storedCode.equals(code)) {
            return false;
        }

        // 验证通过，删除Redis中的验证码（防止重复使用）
        redisTemplate.delete(redisKey);

        // 更新数据库中的验证码状态为已使用（使用 Mapper 方法）
        verificationCodeMapper.markUsed(target, code, scene);

        return true;
    }

    // ====================== 黑名单管理 ======================

    /**
     * 检查是否在黑名单中
     */
    private boolean isInBlacklist(String target) {
        String key = "auth:code:blacklist:" + target;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 加入黑名单（24小时）
     */
    private void addToBlacklist(String target) {
        String key = "auth:code:blacklist:" + target;
        redisTemplate.opsForValue().set(key, "1", 24, TimeUnit.HOURS);
        log.warn("账号已加入验证码黑名单：target={}", target);
    }
}

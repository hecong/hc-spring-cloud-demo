package com.hnhegui.hc.service.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
public class PasswordService {

    /**
     * 密码复杂度正则：8-20位，必须包含大小写字母、数字、特殊符号
     */
    private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+])[A-Za-z\\d!@#$%^&*()_+]{8,20}$";

    /**
     * 弱密码黑名单
     */
    private static final Set<String> WEAK_PASSWORDS = Set.of(
        "123456", "password", "qwerty", "abc123",
        "Password1!", "Admin123!", "Qwerty1!",
        "11111111", "12345678", "87654321",
        "Aa123456!", "P@ssw0rd", "Passw0rd!"
    );

    /**
     * 校验密码复杂度
     *
     * @param password 密码明文
     * @return 校验结果消息，null表示通过
     */
    public String validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return "密码不能为空";
        }
        if (password.length() < 8) {
            return "密码长度不能少于8位";
        }
        if (password.length() > 20) {
            return "密码长度不能超过20位";
        }
        if (!password.matches(PASSWORD_PATTERN)) {
            return "密码需包含大小写字母、数字和特殊符号(!@#$%^&*()_+)";
        }
        if (isWeakPassword(password)) {
            return "密码过于简单，请使用更复杂的密码";
        }
        return null;
    }

    /**
     * 检查是否为弱密码
     *
     * @param password 密码明文
     * @return 是否为弱密码
     */
    public boolean isWeakPassword(String password) {
        if (WEAK_PASSWORDS.contains(password)) {
            return true;
        }
        // 检查是否包含连续数字/字母
        if (containsSequentialChars(password)) {
            return true;
        }
        // 检查是否为手机号片段（连续6位以上数字）
        String digits = password.replaceAll("[^0-9]", "");
        if (digits.length() >= 6 && digits.length() <= 11) {
            return true;
        }
        return false;
    }

    /**
     * 检查是否包含连续字符（如abc, 123, cba, 321）
     */
    private boolean containsSequentialChars(String password) {
        int sequentialLimit = 3;
        String lower = password.toLowerCase();
        for (int i = 0; i < lower.length() - sequentialLimit; i++) {
            char c1 = lower.charAt(i);
            char c2 = lower.charAt(i + 1);
            char c3 = lower.charAt(i + 2);
            // 正序连续
            if (c2 - c1 == 1 && c3 - c2 == 1) {
                return true;
            }
            // 逆序连续
            if (c1 - c2 == 1 && c2 - c3 == 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 校验新密码不能与原密码相同
     *
     * @param rawPassword     新密码明文
     * @param encodedPassword 原密码密文
     * @param passwordEncoder 密码编码器
     * @return 是否相同
     */
    public boolean isSameAsOldPassword(String rawPassword, String encodedPassword,
                                       org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}

package com.hnhegui.hc.context.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hnhegui.hc.context.core.UserContext;
import lombok.Getter;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 用户上下文加解密工具
 * 使用 AES-128-CBC 加密
 *
 * @author hecong
 * @since 2026/4/10
 */
public class UserContextEncryptUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String IV_ALGORITHM = "AES/CBC/PKCS5Padding";
    
    private static final int IV_LENGTH = 16;
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * -- GETTER --
     *  获取加密密钥
     */
    @Getter
    private static String secretKey;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.findAndRegisterModules();
    }

    private UserContextEncryptUtil() {
    }

    /**
     * 设置加密密钥
     */
    public static void setSecretKey(String key) {
        if (key == null || key.length() < 16) {
            throw new IllegalArgumentException("加密密钥长度不能少于16位");
        }
        secretKey = key;
    }

    /**
     * 加密用户上下文
     *
     * @param context 用户上下文
     * @return 加密后的字符串
     */
    public static String encrypt(UserContext context) {
        if (context == null) {
            return null;
        }
        try {
            // 1. 序列化为 JSON
            String json = OBJECT_MAPPER.writeValueAsString(context);
            
            // 2. 生成随机 IV
            byte[] iv = generateIv();
            
            // 3. AES 加密
            byte[] encrypted = aesEncrypt(json.getBytes(StandardCharsets.UTF_8), iv);
            
            // 4. 拼接 IV + 密文，Base64 编码
            byte[] combined = new byte[IV_LENGTH + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, IV_LENGTH);
            System.arraycopy(encrypted, 0, combined, IV_LENGTH, encrypted.length);
            
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("用户上下文加密失败", e);
        }
    }

    /**
     * 解密用户上下文
     *
     * @param encrypted 加密字符串
     * @return 用户上下文
     */
    public static UserContext decrypt(String encrypted) {
        if (encrypted == null || encrypted.isEmpty()) {
            return null;
        }
        try {
            // 1. Base64 解码
            byte[] combined = Base64.getDecoder().decode(encrypted);
            
            if (combined.length < IV_LENGTH) {
                throw new IllegalArgumentException("加密数据格式错误");
            }
            
            // 2. 分离 IV 和密文
            byte[] iv = new byte[IV_LENGTH];
            byte[] cipherText = new byte[combined.length - IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, IV_LENGTH);
            System.arraycopy(combined, IV_LENGTH, cipherText, 0, cipherText.length);
            
            // 3. AES 解密
            byte[] decrypted = aesDecrypt(cipherText, iv);
            
            // 4. 反序列化为对象
            String json = new String(decrypted, StandardCharsets.UTF_8);
            return OBJECT_MAPPER.readValue(json, UserContext.class);
        } catch (Exception e) {
            throw new RuntimeException("用户上下文解密失败", e);
        }
    }

    /**
     * 判断是否为加密字符串
     */
    public static boolean isEncrypted(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        try {
            byte[] decoded = Base64.getDecoder().decode(value);
            // 加密数据包含 IV 头部
            return decoded.length > IV_LENGTH;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 生成随机 IV
     */
    private static byte[] generateIv() {
        byte[] iv = new byte[IV_LENGTH];
        RANDOM.nextBytes(iv);
        return iv;
    }

    /**
     * AES 加密
     */
    private static byte[] aesEncrypt(byte[] data, byte[] iv) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(getKeyBytes(), ALGORITHM);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        
        return cipher.doFinal(data);
    }

    /**
     * AES 解密
     */
    private static byte[] aesDecrypt(byte[] data, byte[] iv) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(getKeyBytes(), ALGORITHM);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        
        Cipher cipher = Cipher.getInstance(IV_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        
        return cipher.doFinal(data);
    }

    /**
     * 获取密钥字节数组
     */
    private static byte[] getKeyBytes() {
        // 密钥不足16字节时填充，溢出时截断
        byte[] keyBytes = new byte[16];
        byte[] source = secretKey.getBytes(StandardCharsets.UTF_8);
        int length = Math.min(source.length, 16);
        System.arraycopy(source, 0, keyBytes, 0, length);
        return keyBytes;
    }

    /**
     * 序列化用户上下文为 JSON（不加密）
     */
    public static String serialize(UserContext context) {
        if (context == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(context);
        } catch (Exception e) {
            throw new RuntimeException("用户上下文序列化失败", e);
        }
    }

    /**
     * 反序列化 JSON 为用户上下文（不解密）
     */
    public static UserContext deserialize(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, UserContext.class);
        } catch (Exception e) {
            throw new RuntimeException("用户上下文反序列化失败", e);
        }
    }
}

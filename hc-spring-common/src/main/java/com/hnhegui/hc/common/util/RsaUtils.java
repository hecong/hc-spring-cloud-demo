package com.hnhegui.hc.common.util;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA加密工具类
 */
public class RsaUtils {

    private static final String ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048;

    /**
     * 生成RSA密钥对
     *
     * @return RSA密钥对
     */
    public static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            keyPairGenerator.initialize(KEY_SIZE);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException("生成RSA密钥对失败", e);
        }
    }

    /**
     * 公钥加密
     *
     * @param plainText       明文
     * @param publicKeyBase64 Base64编码的公钥
     * @return Base64编码的密文
     */
    public static String encrypt(String plainText, String publicKeyBase64) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(publicKeyBase64);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PublicKey publicKey = keyFactory.generatePublic(spec);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("RSA公钥加密失败", e);
        }
    }

    /**
     * 私钥解密
     *
     * @param cipherText       Base64编码的密文
     * @param privateKeyBase64 Base64编码的私钥
     * @return 明文
     */
    public static String decrypt(String cipherText, String privateKeyBase64) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(privateKeyBase64);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PrivateKey privateKey = keyFactory.generatePrivate(spec);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(cipherText));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("RSA私钥解密失败", e);
        }
    }

    /**
     * 获取Base64编码的公钥
     *
     * @param publicKey 公钥
     * @return Base64编码的公钥字符串
     */
    public static String getPublicKeyBase64(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    /**
     * 获取Base64编码的私钥
     *
     * @param privateKey 私钥
     * @return Base64编码的私钥字符串
     */
    public static String getPrivateKeyBase64(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }


    public static void main(String[] args) {
        String encrypt = encrypt("Admin@123", "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAx2iV+O1wd86J3yC4ShHkVO6wMvgZChrzFrGZ4fg9h0FINIohxENKqt1ClWIchAUZUeYU9VaAOb+QvIBESwBQN4rmzx3ZDMGh3I6tRfmjKkEEf0CWfi18gzjqTCVmFX7rg3mxhUiMxVwBWqfevnHrCG4QlFeONI3+ZrbfOOzbW4mo7cAwnk7n8AqS0XiVBpfk7lZhe+1t4Ui6A2qxIaRL9zxeBECq7X8ldmCY0/J3O16IBOPjBZRGs8Lb0IZKD02x0QrodInIHlaQ3LnIfm6VG2KbzDiwoCCqr6bRcXoOwdoyulH6CFPYlvR6lhNYhKKuwNA46fJnPlUsQsjdooaB2QIDAQAB");
        System.out.println(encrypt);
        String text = decrypt(encrypt,"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAx2iV+O1wd86J3yC4ShHkVO6wMvgZChrzFrGZ4fg9h0FINIohxENKqt1ClWIchAUZUeYU9VaAOb+QvIBESwBQN4rmzx3ZDMGh3I6tRfmjKkEEf0CWfi18gzjqTCVmFX7rg3mxhUiMxVwBWqfevnHrCG4QlFeONI3+ZrbfOOzbW4mo7cAwnk7n8AqS0XiVBpfk7lZhe+1t4Ui6A2qxIaRL9zxeBECq7X8ldmCY0/J3O16IBOPjBZRGs8Lb0IZKD02x0QrodInIHlaQ3LnIfm6VG2KbzDiwoCCqr6bRcXoOwdoyulH6CFPYlvR6lhNYhKKuwNA46fJnPlUsQsjdooaB2QIDAQAB");
        System.out.println(text);
    }
}

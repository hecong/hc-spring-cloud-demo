package com.hnhegui.hc.config;

import com.hnhegui.hc.common.util.RsaUtils;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.KeyPair;

/**
 * RSA密钥配置
 * <p>
 * 应用启动时自动生成RSA密钥对，用于登录密码传输加密。
 * 公钥暴露给前端用于加密，私钥保留在服务端用于解密。
 */
@Slf4j
@Component
public class RsaKeyConfig {

    @Getter
    private String publicKeyBase64;
    private String privateKeyBase64;

    @PostConstruct
    public void init() {
        KeyPair keyPair = RsaUtils.generateKeyPair();
        this.publicKeyBase64 = RsaUtils.getPublicKeyBase64(keyPair.getPublic());
        this.privateKeyBase64 = RsaUtils.getPrivateKeyBase64(keyPair.getPrivate());
        log.info("RSA密钥对已生成");
    }

    /**
     * 使用私钥解密
     *
     * @param cipherText Base64编码的密文
     * @return 解密后的明文
     */
    public String decrypt(String cipherText) {
        return RsaUtils.decrypt(cipherText, privateKeyBase64);
    }
}

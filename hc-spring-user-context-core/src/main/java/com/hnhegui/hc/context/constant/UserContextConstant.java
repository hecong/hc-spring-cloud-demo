package com.hnhegui.hc.context.constant;

/**
 * 用户上下文常量
 *
 * @author hecong
 * @since 2026/4/10
 */
public interface UserContextConstant {

    /**
     * 默认 Header 名称
     */
    String DEFAULT_HEADER_NAME = "X-User-Context";

    /**
     * 加密标识 Header 名称（用于标记是否加密）
     */
    String DEFAULT_ENCRYPTED_HEADER_NAME = "X-User-Context-Encrypted";

    /**
     * 加密标识值
     */
    String ENCRYPTED_VALUE = "true";

    /**
     * 配置前缀
     */
    String CONFIG_PREFIX = "hc.user-context";

    /**
     * 加密密钥配置项
     */
    String CONFIG_SECRET_KEY = CONFIG_PREFIX + ".secret-key";

    /**
     * Header 名称配置项
     */
    String CONFIG_HEADER_NAME = CONFIG_PREFIX + ".header-name";

    /**
     * 是否启用配置项
     */
    String CONFIG_ENABLED = CONFIG_PREFIX + ".enabled";
}

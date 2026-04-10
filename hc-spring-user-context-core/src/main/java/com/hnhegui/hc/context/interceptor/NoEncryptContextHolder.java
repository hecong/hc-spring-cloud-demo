package com.hnhegui.hc.context.interceptor;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * NoEncrypt 标记持有者
 * 用于在方法调用链中传递是否不加密的标记
 *
 * @author hecong
 * @since 2026/4/10
 */
public class NoEncryptContextHolder {

    private static final com.alibaba.ttl.TransmittableThreadLocal<Boolean> NO_ENCRYPT_HOLDER = 
        new com.alibaba.ttl.TransmittableThreadLocal<>();

    /**
     * 设置为不加密
     */
    public static void setNoEncrypt() {
        NO_ENCRYPT_HOLDER.set(true);
    }

    /**
     * 判断是否不加密
     */
    public static boolean isNoEncrypt() {
        Boolean value = NO_ENCRYPT_HOLDER.get();
        return value != null && value;
    }

    /**
     * 清除
     */
    public static void clear() {
        NO_ENCRYPT_HOLDER.remove();
    }
}

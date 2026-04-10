package com.hnhegui.hc.context.interceptor;

import com.hnhegui.hc.context.annotation.NoEncrypt;
import com.hnhegui.hc.context.constant.UserContextConstant;
import com.hnhegui.hc.context.core.UserContext;
import com.hnhegui.hc.context.core.UserContextHolder;
import com.hnhegui.hc.context.util.UserContextEncryptUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

/**
 * Feign 用户上下文拦截器
 * 自动将当前用户上下文透传到下游服务
 *
 * @author hecong
 * @since 2026/4/10
 */
@Slf4j
@Component
public class FeignContextInterceptor implements RequestInterceptor {

    private final String headerName;
    private final String encryptedHeaderName;

    public FeignContextInterceptor() {
        this(UserContextConstant.DEFAULT_HEADER_NAME, UserContextConstant.DEFAULT_ENCRYPTED_HEADER_NAME);
    }

    public FeignContextInterceptor(String headerName, String encryptedHeaderName) {
        this.headerName = headerName;
        this.encryptedHeaderName = encryptedHeaderName;
    }

    @Override
    public void apply(RequestTemplate template) {
        // 1. 检查当前线程是否有用户上下文
        UserContext context = UserContextHolder.get();
        if (context == null || !context.isLogin()) {
            return;
        }

        // 2. 检查是否标记为不加密
        boolean noEncrypt = isNoEncrypt(template);
        String value;
        String encryptedFlag;

        if (noEncrypt) {
            // 明文传输
            value = UserContextEncryptUtil.serialize(context);
            encryptedFlag = null;
            log.debug("[Feign上下文] 明文传输，用户: {}", context.getUserId());
        } else {
            // 加密传输
            value = UserContextEncryptUtil.encrypt(context);
            encryptedFlag = UserContextConstant.ENCRYPTED_VALUE;
            log.debug("[Feign上下文] 加密传输，用户: {}", context.getUserId());
        }

        // 3. 添加到 Header
        template.header(headerName, value);
        if (encryptedFlag != null) {
            template.header(encryptedHeaderName, encryptedFlag);
        } else {
            // 移除加密标识（如果是明文传输）
            template.header(encryptedHeaderName, "");
        }
    }

    /**
     * 检查是否标记为不加密
     */
    private boolean isNoEncrypt(RequestTemplate template) {
        // 尝试从方法上获取注解
        String methodName = template.method();
        String url = template.url();
        
        // 由于 RequestInterceptor 无法直接获取注解，这里通过 ThreadLocal 传递标记
        // 使用 NoEncryptContextHolder 来判断
        return NoEncryptContextHolder.isNoEncrypt();
    }

    /**
     * 清除上下文
     */
    public static void clear() {
        UserContextHolder.clear();
    }
}

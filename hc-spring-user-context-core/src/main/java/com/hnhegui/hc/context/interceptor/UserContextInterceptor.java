package com.hnhegui.hc.context.interceptor;

import com.hnhegui.hc.context.core.UserContext;
import com.hnhegui.hc.context.core.UserContextHolder;
import com.hnhegui.hc.context.constant.UserContextConstant;
import com.hnhegui.hc.context.util.UserContextEncryptUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 下游服务用户上下文拦截器
 * 从请求 Header 读取并解密用户上下文，设置到 UserContextHolder
 *
 * @author hecong
 * @since 2026/4/10
 */
@Slf4j
public class UserContextInterceptor implements HandlerInterceptor {

    private final String headerName;
    private final String encryptedHeaderName;

    public UserContextInterceptor() {
        this(UserContextConstant.DEFAULT_HEADER_NAME, UserContextConstant.DEFAULT_ENCRYPTED_HEADER_NAME);
    }

    public UserContextInterceptor(String headerName, String encryptedHeaderName) {
        this.headerName = headerName;
        this.encryptedHeaderName = encryptedHeaderName;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            String encryptedValue = request.getHeader(headerName);
            if (encryptedValue == null || encryptedValue.isEmpty()) {
                return true;
            }

            String isEncrypted = request.getHeader(encryptedHeaderName);
            UserContext context;

            if (UserContextConstant.ENCRYPTED_VALUE.equals(isEncrypted)) {
                // 解密
                context = UserContextEncryptUtil.decrypt(encryptedValue);
            } else {
                // 明文（内网调用场景）
                context = UserContextEncryptUtil.deserialize(encryptedValue);
            }

            if (context != null && context.isLogin()) {
                UserContextHolder.set(context);
                log.debug("[用户上下文] 已设置用户: {}", context.getUserId());
            }

        } catch (Exception e) {
            log.error("[用户上下文] 解析失败", e);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清理上下文，防止内存泄漏
        UserContextHolder.clear();
    }
}

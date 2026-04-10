package com.hnhegui.hc.context.core;

import com.alibaba.ttl.TtlCallable;
import com.alibaba.ttl.TtlRunnable;

import java.util.concurrent.Callable;

/**
 * 用户上下文持有者
 * 使用 TransmittableThreadLocal 实现异步场景下的上下文传递
 *
 * @author hecong
 * @since 2026/4/10
 */
public class UserContextHolder {

    private static final com.alibaba.ttl.TransmittableThreadLocal<UserContext> CONTEXT_HOLDER = 
        new com.alibaba.ttl.TransmittableThreadLocal<>();

    /**
     * 设置用户上下文
     */
    public static void set(UserContext context) {
        CONTEXT_HOLDER.set(context);
    }

    /**
     * 获取用户上下文
     */
    public static UserContext get() {
        return CONTEXT_HOLDER.get();
    }

    /**
     * 获取用户上下文，如果为空则抛出异常
     */
    public static UserContext getRequired() {
        UserContext context = get();
        if (context == null) {
            throw new IllegalStateException("用户上下文为空，请确保请求经过网关且已启用用户上下文");
        }
        return context;
    }

    /**
     * 获取用户ID
     */
    public static Long getUserId() {
        UserContext context = get();
        return context != null ? context.getUserId() : null;
    }

    /**
     * 获取用户名
     */
    public static String getUsername() {
        UserContext context = get();
        return context != null ? context.getUsername() : null;
    }

    /**
     * 清除用户上下文
     */
    public static void clear() {
        CONTEXT_HOLDER.remove();
    }

    /**
     * 判断是否已登录
     */
    public static boolean isLogin() {
        UserContext context = get();
        return context != null && context.isLogin();
    }

    /**
     * 包装 Runnable，支持异步传递
     */
    public static Runnable wrap(Runnable runnable) {
        return TtlRunnable.get(runnable);
    }

    /**
     * 包装 Callable，支持异步传递
     */
    public static <T> Callable<T> wrap(Callable<T> callable) {
        return TtlCallable.get(callable);
    }

    /**
     * 包装 Runnable 并执行
     */
    public static void execute(Runnable runnable) {
        TtlRunnable.get(runnable).run();
    }

    /**
     * 包装 Callable 并执行
     */
    public static <T> T execute(Callable<T> callable) throws Exception {
        return TtlCallable.get(callable).call();
    }
}

package com.hnhegui.hc.context.annotation;

import java.lang.annotation.*;
import org.springframework.context.annotation.Import;

/**
 * 启用用户上下文
 * 加在 Spring Boot 启动类或配置类上，激活用户上下文传递功能
 *
 * @author hecong
 * @since 2026/4/10
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(com.hnhegui.hc.context.config.UserContextAutoConfiguration.class)
public @interface EnableUserContext {

    /**
     * 启用用户上下文传递
     * 默认 true
     */
    boolean enabled() default true;

    /**
     * 启用用户上下文注入（@UserContext 注解生效）
     * 默认 true
     */
    boolean enableInjection() default true;

    /**
     * 启用 Feign 拦截器（自动透传上下文到下游服务）
     * 默认 true
     */
    boolean enableFeignInterceptor() default true;
}

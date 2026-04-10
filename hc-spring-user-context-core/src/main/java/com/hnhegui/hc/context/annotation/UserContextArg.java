package com.hnhegui.hc.context.annotation;

import java.lang.annotation.*;

/**
 * 用户上下文注入注解
 * 用于方法参数上，自动注入当前用户上下文
 *
 * <pre>
 * 示例：
 * public String test(@UserContext UserContext ctx) {
 *     return ctx.getUsername();
 * }
 * </pre>
 *
 * @author hecong
 * @since 2026/4/10
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UserContextArg {

    /**
     * 是否必须
     * 如果为 true 且上下文为空，则抛出异常
     * 默认 true
     */
    boolean required() default true;
}

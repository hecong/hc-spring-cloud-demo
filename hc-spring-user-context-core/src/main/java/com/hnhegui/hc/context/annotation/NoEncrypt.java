package com.hnhegui.hc.context.annotation;

import java.lang.annotation.*;

/**
 * 不加密传输注解
 * 加在方法或类上，该方法调用链中的用户上下文将使用明文传输（不加密）
 * 用于内网高性能场景
 *
 * <pre>
 * 示例：
 * &#64;NoEncrypt
 * &#64;GetMapping("/inner/user/info")
 * public UserInfo getUserInfo(@UserContextArg UserContext ctx) {
 *     // 使用明文传输
 * }
 * </pre>
 *
 * @author hecong
 * @since 2026/4/10
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoEncrypt {
}

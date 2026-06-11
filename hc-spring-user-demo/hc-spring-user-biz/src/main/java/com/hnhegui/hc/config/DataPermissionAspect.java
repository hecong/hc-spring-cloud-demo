package com.hnhegui.hc.config;

import cn.dev33.satoken.annotation.SaCheckOr;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.hc.framework.mybatis.annotation.DataPermission;
import com.hc.framework.mybatis.handler.DataPermissionContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Method;

/**
 * 数据权限 AOP 切面
 *
 * <p>拦截标注了 {@link DataPermission} 的 Controller 方法，
 * 从同方法/类的 {@code @SaCheckPermission} 注解提取权限码，
 * 写入 {@link DataPermissionContextHolder} 供 MyBatis 拦截器读取。</p>
 *
 * <p>权限码提取优先级：方法注解 > 类注解 > {@code @SaCheckOr}</p>
 *
 * @author hecong
 * @since 2026/6/11
 */
@Slf4j
@Aspect
@Order(1)
public class DataPermissionAspect {

    @Around("@within(com.hc.framework.mybatis.annotation.DataPermission) || " +
            "@annotation(com.hc.framework.mybatis.annotation.DataPermission)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        // 1. 解析有效注解（方法优先于类）
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        DataPermission methodDp = method.getAnnotation(DataPermission.class);
        DataPermission classDp = pjp.getTarget().getClass().getAnnotation(DataPermission.class);
        DataPermission effective = (methodDp != null) ? methodDp : classDp;

        // 2. enabled=false 则跳过
        if (effective == null || !effective.enable()) {
            return pjp.proceed();
        }

        // 3. 从 @SaCheckPermission 提取权限码
        String permissionCode = extractPermissionCode(method, pjp.getTarget().getClass());
        if (permissionCode == null) {
            log.warn("[数据权限] 未找到 @SaCheckPermission，跳过 dataPermission on {}.{}",
                pjp.getTarget().getClass().getSimpleName(), method.getName());
            return pjp.proceed();
        }

        // 4. 写入 ThreadLocal
        DataPermissionContextHolder.set(permissionCode);
        try {
            return pjp.proceed();
        } finally {
            DataPermissionContextHolder.clear();
        }
    }

    /**
     * 从方法和类上提取权限码
     */
    private String extractPermissionCode(Method method, Class<?> clazz) {
        // 方法上的 @SaCheckPermission 优先
        SaCheckPermission methodP = method.getAnnotation(SaCheckPermission.class);
        if (methodP != null && methodP.value().length > 0) {
            return methodP.value()[0];
        }
        // 类上的 @SaCheckPermission
        SaCheckPermission classP = clazz.getAnnotation(SaCheckPermission.class);
        if (classP != null && classP.value().length > 0) {
            return classP.value()[0];
        }
        // @SaCheckOr 中取第一个权限码
        SaCheckOr orP = method.getAnnotation(SaCheckOr.class);
        if (orP != null && orP.value().length > 0) {
            for (SaCheckPermission sp : orP.value()) {
                if (sp.value().length > 0) {
                    return sp.value()[0];
                }
            }
        }
        return null;
    }
}

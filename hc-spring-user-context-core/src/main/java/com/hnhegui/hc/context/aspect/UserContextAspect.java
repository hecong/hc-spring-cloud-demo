package com.hnhegui.hc.context.aspect;

import com.hnhegui.hc.context.annotation.NoEncrypt;
import com.hnhegui.hc.context.interceptor.NoEncryptContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 用户上下文切面
 * 处理 @NoEncrypt 注解，设置不加密标记
 *
 * @author hecong
 * @since 2026/4/10
 */
@Slf4j
@Aspect
@Component
@Order(Integer.MAX_VALUE)
public class UserContextAspect {

    /**
     * 处理 @NoEncrypt 注解
     */
    @Around("@annotation(com.hnhegui.hc.context.annotation.NoEncrypt) || @within(com.hnhegui.hc.context.annotation.NoEncrypt)")
    public Object aroundNoEncrypt(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            // 设置不加密标记
            NoEncryptContextHolder.setNoEncrypt();
            log.debug("[用户上下文] 检测到 @NoEncrypt 注解，当前调用链将使用明文传输");
            
            return joinPoint.proceed();
        } finally {
            // 清除标记
            NoEncryptContextHolder.clear();
        }
    }

    /**
     * 检查方法或类上是否有 @NoEncrypt 注解
     */
    private boolean hasNoEncryptAnnotation(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        // 检查方法注解
        NoEncrypt methodAnnotation = method.getAnnotation(NoEncrypt.class);
        if (methodAnnotation != null) {
            return true;
        }
        
        // 检查类注解
        Class<?> targetClass = joinPoint.getTarget().getClass();
        NoEncrypt classAnnotation = targetClass.getAnnotation(NoEncrypt.class);
        return classAnnotation != null;
    }
}

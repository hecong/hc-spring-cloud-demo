package com.hnhegui.hc.context.config;

import com.hnhegui.hc.context.constant.UserContextConstant;
import com.hnhegui.hc.context.interceptor.FeignContextInterceptor;
import com.hnhegui.hc.context.interceptor.UserContextInterceptor;
import com.hnhegui.hc.context.resolver.UserContextArgumentResolver;
import com.hnhegui.hc.context.util.UserContextEncryptUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 用户上下文自动配置
 *
 * @author hecong
 * @since 2026/4/10
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class UserContextAutoConfiguration implements WebMvcConfigurer {

    @Value("${" + UserContextConstant.CONFIG_SECRET_KEY + ":}")
    private String secretKey;

    @Value("${" + UserContextConstant.CONFIG_HEADER_NAME + ":" + UserContextConstant.DEFAULT_HEADER_NAME + "}")
    private String headerName;

    @Value("${" + UserContextConstant.CONFIG_ENABLED + ":true}")
    private boolean enabled;

    @PostConstruct
    public void init() {
        if (enabled && secretKey != null && !secretKey.isEmpty()) {
            UserContextEncryptUtil.setSecretKey(secretKey);
            log.info("[用户上下文] 已启用，加密密钥已配置");
        } else if (enabled) {
            log.warn("[用户上下文] 已启用，但未配置加密密钥，将使用默认密钥（生产环境请配置）");
            UserContextEncryptUtil.setSecretKey("default-key-change-in-prod");
        }
    }

    /**
     * 用户上下文拦截器
     */
    @Bean
    @ConditionalOnProperty(name = UserContextConstant.CONFIG_ENABLED, havingValue = "true", matchIfMissing = true)
    public UserContextInterceptor userContextInterceptor() {
        return new UserContextInterceptor(headerName, UserContextConstant.DEFAULT_ENCRYPTED_HEADER_NAME);
    }

    /**
     * Feign 上下文拦截器
     */
    @Bean
    @ConditionalOnProperty(name = UserContextConstant.CONFIG_ENABLED, havingValue = "true", matchIfMissing = true)
    @ConditionalOnClass(name = "feign.Feign")
    public FeignContextInterceptor feignContextInterceptor() {
        return new FeignContextInterceptor(headerName, UserContextConstant.DEFAULT_ENCRYPTED_HEADER_NAME);
    }

    /**
     * 用户上下文参数解析器
     */
    @Bean
    @ConditionalOnProperty(name = UserContextConstant.CONFIG_ENABLED, havingValue = "true", matchIfMissing = true)
    public UserContextArgumentResolver userContextArgumentResolver() {
        return new UserContextArgumentResolver();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (enabled) {
            registry.addInterceptor(userContextInterceptor())
                    .addPathPatterns("/**");
            log.info("[用户上下文] HTTP 拦截器已注册");
        }
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        if (enabled) {
            resolvers.add(userContextArgumentResolver());
            log.info("[用户上下文] 参数解析器已注册");
        }
    }
}

package com.hnhegui.hc.context.resolver;

import com.hnhegui.hc.context.annotation.UserContextArg;
import com.hnhegui.hc.context.core.UserContext;
import com.hnhegui.hc.context.core.UserContextHolder;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 用户上下文参数解析器
 * 支持 @UserContextArg 注解，将当前用户上下文注入到方法参数
 *
 * @author hecong
 * @since 2026/4/10
 */
@Component
public class UserContextArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(UserContextArg.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, 
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        UserContextArg annotation = parameter.getParameterAnnotation(UserContextArg.class);
        UserContext context = UserContextHolder.get();
        
        if (context == null || !context.isLogin()) {
            if (annotation != null && annotation.required()) {
                throw new IllegalStateException("用户上下文为空，请确保请求经过网关且已启用用户上下文");
            }
            return null;
        }
        
        return context;
    }
}

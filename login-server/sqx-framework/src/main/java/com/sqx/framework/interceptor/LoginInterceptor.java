package com.sqx.framework.interceptor;

import cn.hutool.core.util.StrUtil;
import com.sqx.common.annotation.Login;
import com.sqx.common.constant.ClientType;
import com.sqx.common.exception.BusinessException;
import com.sqx.common.service.TokenValidator;
import com.sqx.common.utils.UserContext;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * App 端 Token 鉴权拦截器
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    private final TokenValidator tokenValidator;

    public LoginInterceptor(TokenValidator tokenValidator) {
        this.tokenValidator = tokenValidator;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        Login login = method.getAnnotation(Login.class);
        if (login == null) {
            login = handlerMethod.getBeanType().getAnnotation(Login.class);
        }
        if (login == null) {
            return true;
        }
        String token = resolveToken(request);
        if (StrUtil.isBlank(token)) {
            throw new BusinessException(401, "请先登录");
        }
        Long accountId = tokenValidator.validateAndGetAccountId(token, ClientType.APP);
        UserContext.setUserId(accountId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        UserContext.clear();
    }

    private String resolveToken(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (StrUtil.isNotBlank(token)) {
            return token;
        }
        String authorization = request.getHeader("Authorization");
        if (StrUtil.isNotBlank(authorization) && authorization.startsWith("Bearer ")) {
            return authorization.substring(7).trim();
        }
        return null;
    }
}

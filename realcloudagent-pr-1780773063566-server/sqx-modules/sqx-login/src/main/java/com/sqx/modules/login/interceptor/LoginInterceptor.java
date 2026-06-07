package com.sqx.modules.login.interceptor;

import cn.hutool.core.util.StrUtil;
import com.sqx.common.annotation.Login;
import com.sqx.common.exception.BusinessException;
import com.sqx.common.utils.UserContext;
import com.sqx.modules.login.service.TokenService;
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

    private final TokenService tokenService;

    public LoginInterceptor(TokenService tokenService) {
        this.tokenService = tokenService;
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
        Long accountId = tokenService.validateToken(token);
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
        if (StrUtil.isBlank(token)) {
            String authorization = request.getHeader("Authorization");
            if (StrUtil.isNotBlank(authorization) && authorization.startsWith("Bearer ")) {
                token = authorization.substring(7);
            }
        }
        if (StrUtil.isBlank(token)) {
            throw new BusinessException(401, "请先登录");
        }
        return token;
    }
}

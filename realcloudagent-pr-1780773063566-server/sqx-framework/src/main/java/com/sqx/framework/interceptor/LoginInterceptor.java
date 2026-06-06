package com.sqx.framework.interceptor;

import cn.hutool.core.util.StrUtil;
import com.sqx.common.annotation.Login;
import com.sqx.common.exception.BusinessException;
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
        String token = request.getHeader("token");
        if (StrUtil.isBlank(token)) {
            throw new BusinessException(401, "请先登录");
        }
        Long userId = parseUserId(token);
        UserContext.setUserId(userId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        UserContext.clear();
    }

    private Long parseUserId(String token) {
        if (token.startsWith("uid:")) {
            try {
                return Long.parseLong(token.substring(4));
            } catch (NumberFormatException ex) {
                throw new BusinessException(401, "登录态无效");
            }
        }
        throw new BusinessException(401, "登录态无效");
    }
}

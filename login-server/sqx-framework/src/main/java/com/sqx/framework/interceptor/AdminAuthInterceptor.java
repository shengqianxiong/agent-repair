package com.sqx.framework.interceptor;

import cn.hutool.core.util.StrUtil;
import com.sqx.common.constant.ClientType;
import com.sqx.common.exception.BusinessException;
import com.sqx.common.service.TokenValidator;
import com.sqx.common.utils.UserContext;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 管理端 Token 鉴权拦截器，校验管理员登录态。
 */
@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    private final TokenValidator tokenValidator;

    public AdminAuthInterceptor(TokenValidator tokenValidator) {
        this.tokenValidator = tokenValidator;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = resolveToken(request);
        if (StrUtil.isBlank(token)) {
            throw new BusinessException(401, "请先登录");
        }
        Long accountId = tokenValidator.validateAndGetAccountId(token, ClientType.ADMIN);
        UserContext.setUserId(accountId);
        bindShiroSubject(accountId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            subject.logout();
        }
        UserContext.clear();
    }

    private void bindShiroSubject(Long accountId) {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            UsernamePasswordToken shiroToken = new UsernamePasswordToken(
                    String.valueOf(accountId), "admin-session");
            subject.login(shiroToken);
        }
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

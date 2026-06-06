package com.sqx.framework.interceptor;

import cn.hutool.core.util.StrUtil;
import com.sqx.common.exception.BusinessException;
import com.sqx.common.utils.AdminContext;
import com.sqx.common.service.TokenValidator;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 管理端 Token 鉴权拦截器，除登录接口外均需校验
 */
@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    private final TokenValidator tokenValidator;

    public AdminAuthInterceptor(TokenValidator tokenValidator) {
        this.tokenValidator = tokenValidator;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("token");
        if (StrUtil.isBlank(token)) {
            throw new BusinessException(401, "请先登录");
        }
        Long accountId = tokenValidator.validateToken(token);
        AdminContext.setAccountId(accountId);
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            subject.login(new UsernamePasswordToken("admin", token));
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        AdminContext.clear();
    }
}

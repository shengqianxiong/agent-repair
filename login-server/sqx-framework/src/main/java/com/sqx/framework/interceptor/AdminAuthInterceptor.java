package com.sqx.framework.interceptor;

import cn.hutool.core.util.StrUtil;
import com.sqx.common.exception.BusinessException;
import com.sqx.common.utils.AdminContext;
import com.sqx.framework.token.TokenInfo;
import com.sqx.framework.token.TokenService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 管理端 Token 鉴权拦截器，校验管理员权限
 */
@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;

    public AdminAuthInterceptor(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("token");
        if (StrUtil.isBlank(token)) {
            token = request.getParameter("token");
        }
        TokenInfo tokenInfo = tokenService.validateToken(token);
        if (tokenInfo.getRole() == null || tokenInfo.getRole() != 1) {
            throw new BusinessException(403, "无访问权限");
        }
        AdminContext.setAdminId(tokenInfo.getAccountId());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        AdminContext.clear();
    }
}

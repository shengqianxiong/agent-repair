package com.sqx.framework.interceptor;

import cn.hutool.core.util.StrUtil;
import com.sqx.common.annotation.AdminLogin;
import com.sqx.common.constants.AccountConstants;
import com.sqx.common.exception.BusinessException;
import com.sqx.common.utils.AdminContext;
import com.sqx.framework.token.TokenInfo;
import com.sqx.framework.token.TokenService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 管理端 Token 鉴权拦截器，校验管理员身份。
 */
@Component
public class AdminLoginInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;

    public AdminLoginInterceptor(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        AdminLogin adminLogin = method.getAnnotation(AdminLogin.class);
        if (adminLogin == null) {
            adminLogin = handlerMethod.getBeanType().getAnnotation(AdminLogin.class);
        }
        if (adminLogin == null) {
            return true;
        }
        TokenInfo tokenInfo = resolveAdminToken(request);
        bindShiroSubject(tokenInfo);
        AdminContext.setAccountId(tokenInfo.getAccountId());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        AdminContext.clear();
    }

    private TokenInfo resolveAdminToken(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (StrUtil.isBlank(token)) {
            throw new BusinessException(401, "请先登录");
        }
        TokenInfo tokenInfo = tokenService.getTokenInfo(token);
        if (tokenInfo == null) {
            throw new BusinessException(401, "登录已过期，请重新登录");
        }
        if (tokenInfo.getRole() == null || tokenInfo.getRole() != AccountConstants.ROLE_ADMIN) {
            throw new BusinessException(403, "权限不足");
        }
        return tokenInfo;
    }

    private void bindShiroSubject(TokenInfo tokenInfo) {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            UsernamePasswordToken shiroToken = new UsernamePasswordToken(
                    tokenInfo.getUsername(), "admin-token");
            subject.login(shiroToken);
        }
    }
}

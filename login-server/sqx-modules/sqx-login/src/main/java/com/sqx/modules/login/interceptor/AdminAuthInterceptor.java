package com.sqx.modules.login.interceptor;

import cn.hutool.core.util.StrUtil;
import com.sqx.common.annotation.AdminLogin;
import com.sqx.common.constant.AccountConstant;
import com.sqx.common.exception.BusinessException;
import com.sqx.common.utils.UserContext;
import com.sqx.modules.login.db.entity.LoginAccount;
import com.sqx.modules.login.service.AccountService;
import com.sqx.modules.login.service.TokenService;
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
 * 管理端 Token 鉴权拦截器
 */
@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;
    private final AccountService accountService;

    public AdminAuthInterceptor(TokenService tokenService, AccountService accountService) {
        this.tokenService = tokenService;
        this.accountService = accountService;
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
        String token = resolveToken(request);
        if (StrUtil.isBlank(token)) {
            throw new BusinessException(401, "请先登录");
        }
        Long accountId = tokenService.validateAndGetAccountId(token);
        LoginAccount account = accountService.getById(accountId);
        if (account == null) {
            throw new BusinessException(401, "登录态无效");
        }
        if (account.getRole() != AccountConstant.ROLE_ADMIN) {
            throw new BusinessException(403, "无访问权限");
        }
        loginShiroSubject(account.getUsername());
        UserContext.setUserId(accountId);
        return true;
    }

    private void loginShiroSubject(String username) {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            subject.login(new UsernamePasswordToken(username, ""));
        }
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
            return authorization.substring(7);
        }
        return null;
    }
}

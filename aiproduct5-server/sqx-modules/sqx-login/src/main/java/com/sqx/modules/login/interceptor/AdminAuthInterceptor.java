package com.sqx.modules.login.interceptor;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.common.annotation.AdminLogin;
import com.sqx.common.constant.AccountConstant;
import com.sqx.common.exception.BusinessException;
import com.sqx.common.utils.UserContext;
import com.sqx.modules.login.db.entity.LoginAccount;
import com.sqx.modules.login.db.mapper.LoginAccountMapper;
import com.sqx.modules.login.service.TokenService;
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
    private final LoginAccountMapper loginAccountMapper;

    public AdminAuthInterceptor(TokenService tokenService, LoginAccountMapper loginAccountMapper) {
        this.tokenService = tokenService;
        this.loginAccountMapper = loginAccountMapper;
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
        String token = request.getHeader("token");
        if (StrUtil.isBlank(token)) {
            throw new BusinessException(401, "请先登录");
        }
        Long accountId = tokenService.validateToken(token);
        LoginAccount account = loginAccountMapper.selectOne(new LambdaQueryWrapper<LoginAccount>()
                .eq(LoginAccount::getId, accountId));
        if (account == null) {
            throw new BusinessException(401, "登录已过期，请重新登录");
        }
        if (AccountConstant.ROLE_ADMIN != account.getRole()) {
            throw new BusinessException(403, "权限不足");
        }
        if (AccountConstant.STATUS_DISABLED == account.getStatus()) {
            throw new BusinessException("账号已被禁用");
        }
        UserContext.setUserId(accountId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        UserContext.clear();
    }
}

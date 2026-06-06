package com.sqx.modules.login.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.common.exception.BusinessException;
import com.sqx.framework.token.TokenService;
import com.sqx.modules.login.constant.AccountRole;
import com.sqx.modules.login.constant.AccountStatus;
import com.sqx.modules.login.db.entity.LoginAccount;
import com.sqx.modules.login.db.mapper.LoginAccountMapper;
import com.sqx.modules.login.domain.bo.LoginBo;
import com.sqx.modules.login.domain.vo.LoginVo;
import com.sqx.modules.login.service.LoginAuthService;
import com.sqx.modules.login.util.PasswordUtil;
import org.springframework.stereotype.Service;

/**
 * 登录认证服务实现
 */
@Service
public class LoginAuthServiceImpl implements LoginAuthService {

    private final LoginAccountMapper accountMapper;
    private final TokenService tokenService;

    public LoginAuthServiceImpl(LoginAccountMapper accountMapper, TokenService tokenService) {
        this.accountMapper = accountMapper;
        this.tokenService = tokenService;
    }

    @Override
    public LoginVo appLogin(LoginBo loginBo) {
        LoginAccount account = authenticate(loginBo);
        return buildLoginVo(account);
    }

    @Override
    public LoginVo adminLogin(LoginBo loginBo) {
        LoginAccount account = authenticate(loginBo);
        if (!AccountRole.isAdmin(account.getRole())) {
            throw new BusinessException(403, "无访问权限");
        }
        return buildLoginVo(account);
    }

    /**
     * 校验账号密码并返回账号实体
     */
    private LoginAccount authenticate(LoginBo loginBo) {
        validateLoginBo(loginBo);
        LoginAccount account = accountMapper.selectOne(new LambdaQueryWrapper<LoginAccount>()
                .eq(LoginAccount::getUsername, loginBo.getUsername().trim()));
        if (account == null) {
            throw new BusinessException("账号或密码错误");
        }
        if (!AccountStatus.isNormal(account.getStatus())) {
            throw new BusinessException("账号已被禁用");
        }
        if (!PasswordUtil.matches(loginBo.getPassword(), account.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }
        return account;
    }

    private void validateLoginBo(LoginBo loginBo) {
        if (loginBo == null) {
            throw new BusinessException("请输入账号/密码");
        }
        if (StrUtil.isBlank(loginBo.getUsername())) {
            throw new BusinessException("请输入账号");
        }
        if (StrUtil.isBlank(loginBo.getPassword())) {
            throw new BusinessException("请输入密码");
        }
    }

    private LoginVo buildLoginVo(LoginAccount account) {
        String token = tokenService.createToken(account.getId(), account.getRole());
        LoginVo loginVo = new LoginVo();
        loginVo.setToken(token);
        loginVo.setExpireAt(System.currentTimeMillis() + TokenService.DEFAULT_EXPIRE_MILLIS);
        loginVo.setAccountId(account.getId());
        loginVo.setUsername(account.getUsername());
        return loginVo;
    }
}

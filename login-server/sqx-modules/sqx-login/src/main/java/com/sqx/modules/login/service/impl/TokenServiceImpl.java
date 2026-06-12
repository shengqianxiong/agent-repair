package com.sqx.modules.login.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.common.constant.AccountConstant;
import com.sqx.common.exception.BusinessException;
import com.sqx.modules.login.db.entity.LoginToken;
import com.sqx.modules.login.db.mapper.LoginTokenMapper;
import com.sqx.modules.login.service.TokenService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Token 服务实现
 */
@Service
public class TokenServiceImpl implements TokenService {

    private final LoginTokenMapper loginTokenMapper;

    public TokenServiceImpl(LoginTokenMapper loginTokenMapper) {
        this.loginTokenMapper = loginTokenMapper;
    }

    @Override
    public String createToken(Long accountId) {
        loginTokenMapper.delete(new LambdaQueryWrapper<LoginToken>()
                .eq(LoginToken::getAccountId, accountId));

        LoginToken loginToken = new LoginToken();
        loginToken.setAccountId(accountId);
        loginToken.setToken(IdUtil.simpleUUID());
        loginToken.setExpireTime(LocalDateTime.now().plusHours(AccountConstant.TOKEN_EXPIRE_HOURS));
        loginToken.setCreateTime(LocalDateTime.now());
        loginTokenMapper.insert(loginToken);
        return loginToken.getToken();
    }

    @Override
    public Long validateToken(String token) {
        LoginToken loginToken = findValidToken(token);
        if (loginToken == null) {
            throw new BusinessException(401, "登录已过期，请重新登录");
        }
        return loginToken.getAccountId();
    }

    private LoginToken findValidToken(String token) {
        if (StrUtil.isBlank(token)) {
            return null;
        }
        LoginToken loginToken = loginTokenMapper.selectOne(new LambdaQueryWrapper<LoginToken>()
                .eq(LoginToken::getToken, token));
        if (loginToken == null || loginToken.getExpireTime().isBefore(LocalDateTime.now())) {
            return null;
        }
        return loginToken;
    }
}

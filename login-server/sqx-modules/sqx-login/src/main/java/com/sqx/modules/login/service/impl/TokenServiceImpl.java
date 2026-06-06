package com.sqx.modules.login.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.common.exception.BusinessException;
import com.sqx.common.service.TokenValidator;
import com.sqx.modules.login.db.entity.LoginToken;
import com.sqx.modules.login.db.mapper.LoginTokenMapper;
import com.sqx.modules.login.service.TokenService;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

/**
 * 会话 Token 管理服务实现
 */
@Service
public class TokenServiceImpl implements TokenService, TokenValidator {

    private static final int TOKEN_EXPIRE_HOURS = 2;

    private final LoginTokenMapper tokenMapper;

    public TokenServiceImpl(LoginTokenMapper tokenMapper) {
        this.tokenMapper = tokenMapper;
    }

    @Override
    public String createToken(Long accountId, int clientType) {
        Date now = new Date();
        LoginToken loginToken = new LoginToken();
        loginToken.setAccountId(accountId);
        loginToken.setToken(IdUtil.simpleUUID());
        loginToken.setClientType(clientType);
        loginToken.setExpireTime(calculateExpireTime(now));
        loginToken.setCreateTime(now);
        tokenMapper.insert(loginToken);
        return loginToken.getToken();
    }

    @Override
    public Long validateToken(String token, int clientType) {
        return validateAndGetAccountId(token, clientType);
    }

    @Override
    public Long validateAndGetAccountId(String token, int clientType) {
        if (StrUtil.isBlank(token)) {
            throw new BusinessException(401, "请先登录");
        }
        LoginToken loginToken = tokenMapper.selectOne(new LambdaQueryWrapper<LoginToken>()
                .eq(LoginToken::getToken, token)
                .eq(LoginToken::getClientType, clientType));
        if (loginToken == null) {
            throw new BusinessException(401, "登录态无效");
        }
        if (loginToken.getExpireTime().before(new Date())) {
            throw new BusinessException(401, "登录已过期，请重新登录");
        }
        return loginToken.getAccountId();
    }

    private Date calculateExpireTime(Date baseTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(baseTime);
        calendar.add(Calendar.HOUR_OF_DAY, TOKEN_EXPIRE_HOURS);
        return calendar.getTime();
    }
}

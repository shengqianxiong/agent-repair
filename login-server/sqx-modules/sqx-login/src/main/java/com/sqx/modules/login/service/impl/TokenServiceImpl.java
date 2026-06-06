package com.sqx.modules.login.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.common.constant.AccountConstant;
import com.sqx.common.exception.BusinessException;
import com.sqx.modules.login.db.entity.LoginToken;
import com.sqx.modules.login.db.mapper.LoginTokenMapper;
import com.sqx.modules.login.service.TokenService;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

/**
 * 会话 Token 服务实现
 */
@Service
public class TokenServiceImpl implements TokenService {

    private final LoginTokenMapper tokenMapper;

    public TokenServiceImpl(LoginTokenMapper tokenMapper) {
        this.tokenMapper = tokenMapper;
    }

    @Override
    public String createToken(Long accountId) {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.HOUR_OF_DAY, AccountConstant.TOKEN_EXPIRE_HOURS);

        LoginToken loginToken = new LoginToken();
        loginToken.setToken(IdUtil.simpleUUID());
        loginToken.setAccountId(accountId);
        loginToken.setExpireTime(calendar.getTime());
        loginToken.setCreateTime(now);
        tokenMapper.insert(loginToken);
        return loginToken.getToken();
    }

    @Override
    public Long validateAndGetAccountId(String token) {
        LoginToken loginToken = tokenMapper.selectOne(new LambdaQueryWrapper<LoginToken>()
                .eq(LoginToken::getToken, token));
        if (loginToken == null) {
            throw new BusinessException(401, "登录态无效");
        }
        if (loginToken.getExpireTime().before(new Date())) {
            tokenMapper.deleteById(loginToken.getId());
            throw new BusinessException(401, "登录已过期，请重新登录");
        }
        return loginToken.getAccountId();
    }
}

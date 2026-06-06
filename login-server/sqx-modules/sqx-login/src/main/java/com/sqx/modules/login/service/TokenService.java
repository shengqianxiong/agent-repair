package com.sqx.modules.login.service;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.common.exception.BusinessException;
import com.sqx.common.service.TokenValidator;
import com.sqx.modules.login.db.entity.LoginToken;
import com.sqx.modules.login.db.mapper.LoginTokenMapper;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 会话 Token 服务，默认有效期 2 小时
 */
@Service
public class TokenService implements TokenValidator {

    /** Token 默认有效期：2 小时（毫秒） */
    private static final long TOKEN_EXPIRE_MILLIS = 2 * 60 * 60 * 1000L;

    private final LoginTokenMapper loginTokenMapper;

    public TokenService(LoginTokenMapper loginTokenMapper) {
        this.loginTokenMapper = loginTokenMapper;
    }

    /**
     * 为账号生成会话 Token
     *
     * @param accountId 账号主键
     * @return Token 字符串
     */
    public String createToken(Long accountId) {
        Date now = new Date();
        LoginToken loginToken = new LoginToken();
        loginToken.setAccountId(accountId);
        loginToken.setToken(IdUtil.simpleUUID());
        loginToken.setExpireTime(new Date(now.getTime() + TOKEN_EXPIRE_MILLIS));
        loginToken.setCreateTime(now);
        loginTokenMapper.insert(loginToken);
        return loginToken.getToken();
    }

    @Override
    public Long validateToken(String token) {
        LambdaQueryWrapper<LoginToken> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LoginToken::getToken, token);
        LoginToken loginToken = loginTokenMapper.selectOne(wrapper);
        if (loginToken == null) {
            throw new BusinessException(401, "登录态无效，请重新登录");
        }
        if (loginToken.getExpireTime().before(new Date())) {
            loginTokenMapper.deleteById(loginToken.getId());
            throw new BusinessException(401, "登录已过期，请重新登录");
        }
        return loginToken.getAccountId();
    }

    /**
     * 注销 Token
     *
     * @param token 会话 Token
     */
    public void revokeToken(String token) {
        LambdaQueryWrapper<LoginToken> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LoginToken::getToken, token);
        loginTokenMapper.delete(wrapper);
    }
}

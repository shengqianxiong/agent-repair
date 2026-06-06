package com.sqx.framework.token;

import cn.hutool.core.util.IdUtil;
import com.sqx.common.constants.AccountConstants;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于内存的 Token 会话管理，默认有效期 2 小时。
 */
@Service
public class TokenServiceImpl implements TokenService {

    private final Map<String, TokenInfo> tokenStore = new ConcurrentHashMap<>();

    @Override
    public String createToken(Long accountId, String username, Integer role) {
        String token = IdUtil.simpleUUID();
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setAccountId(accountId);
        tokenInfo.setUsername(username);
        tokenInfo.setRole(role);
        tokenInfo.setExpireTime(new Date(System.currentTimeMillis()
                + AccountConstants.TOKEN_EXPIRE_HOURS * 3600_000L));
        tokenStore.put(token, tokenInfo);
        return token;
    }

    @Override
    public TokenInfo getTokenInfo(String token) {
        TokenInfo tokenInfo = tokenStore.get(token);
        if (tokenInfo == null) {
            return null;
        }
        if (tokenInfo.getExpireTime().before(new Date())) {
            tokenStore.remove(token);
            return null;
        }
        return tokenInfo;
    }

    @Override
    public void removeToken(String token) {
        tokenStore.remove(token);
    }
}

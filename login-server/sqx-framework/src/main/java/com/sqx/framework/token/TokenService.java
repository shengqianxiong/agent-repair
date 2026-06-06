package com.sqx.framework.token;

import com.sqx.common.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会话 Token 管理服务，默认有效期 2 小时
 */
@Service
public class TokenService {

    /** Token 默认有效期：2 小时（毫秒） */
    public static final long DEFAULT_EXPIRE_MILLIS = 2 * 60 * 60 * 1000L;

    private final Map<String, TokenInfo> tokenStore = new ConcurrentHashMap<>();

    /**
     * 创建并缓存 Token
     *
     * @param accountId 账号ID
     * @param role      角色
     * @return Token 字符串
     */
    public String createToken(Long accountId, Integer role) {
        return createToken(accountId, role, DEFAULT_EXPIRE_MILLIS);
    }

    /**
     * 创建并缓存 Token
     *
     * @param accountId    账号ID
     * @param role         角色
     * @param expireMillis 有效期（毫秒）
     * @return Token 字符串
     */
    public String createToken(Long accountId, Integer role, long expireMillis) {
        String token = UUID.randomUUID().toString().replace("-", "");
        long expireAt = System.currentTimeMillis() + expireMillis;
        tokenStore.put(token, new TokenInfo(accountId, role, expireAt));
        return token;
    }

    /**
     * 校验 Token 并返回会话信息
     *
     * @param token Token 字符串
     * @return 会话信息
     */
    public TokenInfo validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new BusinessException(401, "请先登录");
        }
        TokenInfo tokenInfo = tokenStore.get(token);
        if (tokenInfo == null) {
            throw new BusinessException(401, "登录态无效，请重新登录");
        }
        if (System.currentTimeMillis() > tokenInfo.getExpireAt()) {
            tokenStore.remove(token);
            throw new BusinessException(401, "登录已过期，请重新登录");
        }
        return tokenInfo;
    }

    /**
     * 移除 Token（登出场景）
     *
     * @param token Token 字符串
     */
    public void removeToken(String token) {
        if (token != null) {
            tokenStore.remove(token);
        }
    }

    /**
     * 清空所有 Token（测试使用）
     */
    public void clearAll() {
        tokenStore.clear();
    }
}

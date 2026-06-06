package com.sqx.framework.token;

/**
 * 登录 Token 管理服务。
 */
public interface TokenService {

    /**
     * 为指定账号创建会话 Token。
     *
     * @param accountId 账号主键
     * @param username 账号名称
     * @param role 角色
     * @return Token 字符串
     */
    String createToken(Long accountId, String username, Integer role);

    /**
     * 根据 Token 获取会话信息，不存在或已过期时返回 null。
     *
     * @param token Token 字符串
     * @return 会话信息
     */
    TokenInfo getTokenInfo(String token);

    /**
     * 移除 Token，用于登出或强制失效。
     *
     * @param token Token 字符串
     */
    void removeToken(String token);
}

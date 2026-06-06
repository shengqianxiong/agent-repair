package com.sqx.modules.login.service;

/**
 * 会话 Token 服务
 */
public interface TokenService {

    /**
     * 为指定账号创建会话 Token
     *
     * @param accountId 账号ID
     * @return Token 字符串
     */
    String createToken(Long accountId);

    /**
     * 校验 Token 并返回账号ID
     *
     * @param token 会话 Token
     * @return 账号ID
     */
    Long validateAndGetAccountId(String token);
}

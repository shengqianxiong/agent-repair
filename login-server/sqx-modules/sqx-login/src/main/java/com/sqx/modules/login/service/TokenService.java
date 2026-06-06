package com.sqx.modules.login.service;

/**
 * 会话 Token 管理服务
 */
public interface TokenService {

    /**
     * 创建会话 Token，默认有效期 2 小时
     */
    String createToken(Long accountId, int clientType);

    /**
     * 校验 Token 并返回账号 ID
     */
    Long validateToken(String token, int clientType);
}

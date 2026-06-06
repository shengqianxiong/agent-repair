package com.sqx.modules.login.service;

import com.sqx.modules.login.db.entity.LoginAccount;

/**
 * Token 服务
 */
public interface TokenService {

    /**
     * 为账号创建会话 Token
     */
    String createToken(Long accountId);

    /**
     * 校验 Token 并返回账号 ID，无效或过期抛出业务异常
     */
    Long validateToken(String token);
}

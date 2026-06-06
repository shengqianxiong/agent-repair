package com.sqx.common.service;

/**
 * Token 校验服务接口
 */
public interface TokenValidator {

    /**
     * 校验 Token 并返回账号 ID
     *
     * @param token 会话 Token
     * @return 账号主键
     */
    Long validateToken(String token);
}

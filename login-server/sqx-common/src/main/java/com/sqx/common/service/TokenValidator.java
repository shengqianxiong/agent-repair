package com.sqx.common.service;

/**
 * Token 校验服务，由业务模块实现并在运行时注入鉴权拦截器。
 */
public interface TokenValidator {

    /**
     * 校验 Token 并返回账号 ID。
     *
     * @param token      请求头中的 Token
     * @param clientType 客户端类型，见 {@link com.sqx.common.constant.ClientType}
     * @return 账号 ID
     */
    Long validateAndGetAccountId(String token, int clientType);
}

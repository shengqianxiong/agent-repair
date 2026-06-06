package com.sqx.modules.login.domain.vo;

import lombok.Data;

/**
 * 登录成功响应
 */
@Data
public class LoginVo {

    /** 会话 Token */
    private String token;

    /** 过期时间戳（毫秒） */
    private Long expireAt;

    /** 账号ID */
    private Long accountId;

    /** 登录账号 */
    private String username;
}

package com.sqx.modules.login.domain.vo;

import lombok.Data;

/**
 * 登录响应
 */
@Data
public class LoginVo {

    /** 会话 Token */
    private String token;

    /** 账号ID */
    private Long accountId;

    /** 账号 */
    private String username;
}

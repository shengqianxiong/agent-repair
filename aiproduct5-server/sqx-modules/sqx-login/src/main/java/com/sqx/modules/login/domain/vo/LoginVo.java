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
    private Long id;

    /** 登录账号 */
    private String account;
}

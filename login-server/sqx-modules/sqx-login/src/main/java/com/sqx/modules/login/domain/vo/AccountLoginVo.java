package com.sqx.modules.login.domain.vo;

import lombok.Data;

/**
 * 登录成功响应
 */
@Data
public class AccountLoginVo {

    /** 会话 Token */
    private String token;

    /** 账号主键 */
    private Long accountId;

    /** 登录账号 */
    private String username;
}

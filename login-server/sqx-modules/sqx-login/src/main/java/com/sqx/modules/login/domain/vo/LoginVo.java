package com.sqx.modules.login.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录成功响应
 */
@Data
public class LoginVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 会话 Token，默认有效期 2 小时 */
    private String token;

    /** 账号ID */
    private Long accountId;

    /** 登录账号 */
    private String username;
}

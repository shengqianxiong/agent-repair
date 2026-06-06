package com.sqx.modules.login.domain.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * 账号登录请求
 */
@Data
public class AccountLoginBo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 登录账号 */
    private String username;

    /** 登录密码 */
    private String password;
}

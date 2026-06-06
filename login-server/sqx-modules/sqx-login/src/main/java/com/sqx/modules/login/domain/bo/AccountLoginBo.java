package com.sqx.modules.login.domain.bo;

import lombok.Data;

/**
 * 账号登录请求
 */
@Data
public class AccountLoginBo {

    /** 登录账号 */
    private String username;

    /** 登录密码（明文，服务端 MD5 校验） */
    private String password;
}

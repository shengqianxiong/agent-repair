package com.sqx.modules.login.domain.bo;

import lombok.Data;

/**
 * 登录请求参数
 */
@Data
public class LoginBo {

    /** 登录账号 */
    private String username;

    /** 登录密码 */
    private String password;
}

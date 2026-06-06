package com.sqx.modules.login.domain.bo;

import lombok.Data;

/**
 * 登录请求参数
 */
@Data
public class LoginBo {

    /** 账号 */
    private String username;

    /** 密码 */
    private String password;
}

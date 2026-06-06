package com.sqx.modules.auth.domain.bo;

import lombok.Data;

/**
 * 登录请求参数
 */
@Data
public class LoginBo {

    /** 用户名 */
    private String username;

    /** 密码 */
    private String password;
}

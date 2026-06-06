package com.sqx.modules.auth.domain.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录请求参数。
 */
@Data
public class LoginBo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 登录用户名 */
    private String username;

    /** 登录密码 */
    private String password;
}

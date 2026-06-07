package com.sqx.modules.login.domain.bo;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

/**
 * 登录请求参数
 */
@Data
public class LoginBo {

    /** 登录用户名（兼容 account 字段） */
    @JsonAlias({"account"})
    private String username;

    /** 登录密码 */
    private String password;
}

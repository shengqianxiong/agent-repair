package com.sqx.modules.login.domain.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * 新增账号请求
 */
@Data
public class AccountSaveBo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 登录账号 */
    private String username;

    /** 初始密码 */
    private String password;
}

package com.sqx.modules.login.domain.bo;

import lombok.Data;

/**
 * 新增账号请求
 */
@Data
public class AccountSaveBo {

    /** 登录账号 */
    private String username;

    /** 初始密码（明文） */
    private String password;
}

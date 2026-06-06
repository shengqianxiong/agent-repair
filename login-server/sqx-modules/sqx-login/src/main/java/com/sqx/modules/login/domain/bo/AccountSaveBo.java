package com.sqx.modules.login.domain.bo;

import lombok.Data;

/**
 * 新增账号参数
 */
@Data
public class AccountSaveBo {

    /** 登录账号 */
    private String username;

    /** 初始密码 */
    private String password;
}

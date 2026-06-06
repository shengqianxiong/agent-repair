package com.sqx.modules.auth.domain.vo;

import lombok.Data;

/**
 * 登录响应数据
 */
@Data
public class LoginVo {

    /** 访问令牌 */
    private String token;

    /** 用户名 */
    private String username;
}

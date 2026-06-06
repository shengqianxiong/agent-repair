package com.sqx.modules.auth.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录成功响应数据。
 */
@Data
public class LoginVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 访问令牌，后续请求放入 Header: token */
    private String token;

    /** 用户ID */
    private Long userId;

    /** 登录用户名 */
    private String username;

    /** 用户昵称 */
    private String nickname;
}

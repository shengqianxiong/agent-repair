package com.sqx.modules.login.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录成功响应。
 */
@Data
public class AccountLoginVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 会话 Token，请求头 token 携带 */
    private String token;

    /** 账号主键 */
    private Long accountId;

    /** 登录账号 */
    private String username;

    /** 角色：1-管理员，0-普通用户 */
    private Integer role;
}

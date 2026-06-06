package com.sqx.framework.token;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 登录 Token 会话信息。
 */
@Data
public class TokenInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 账号主键 */
    private Long accountId;

    /** 账号名称 */
    private String username;

    /** 角色：0-普通用户，1-管理员 */
    private Integer role;

    /** 过期时间 */
    private Date expireTime;
}

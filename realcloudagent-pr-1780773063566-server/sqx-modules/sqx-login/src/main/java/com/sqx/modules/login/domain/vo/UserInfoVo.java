package com.sqx.modules.login.domain.vo;

import lombok.Data;

/**
 * 当前登录用户信息
 */
@Data
public class UserInfoVo {

    /** 用户ID */
    private Long id;

    /** 登录用户名 */
    private String username;
}

package com.sqx.modules.login.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * 账号信息响应
 */
@Data
public class AccountVo {

    /** 账号ID */
    private Long id;

    /** 账号 */
    private String username;

    /** 角色：0-普通用户，1-管理员 */
    private Integer role;

    /** 状态：0-禁用，1-正常 */
    private Integer status;

    /** 创建时间 */
    private Date createTime;
}

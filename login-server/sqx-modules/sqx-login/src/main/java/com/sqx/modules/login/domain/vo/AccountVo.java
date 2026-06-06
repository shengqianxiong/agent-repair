package com.sqx.modules.login.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * 账号信息视图
 */
@Data
public class AccountVo {

    /** 账号ID */
    private Long id;

    /** 登录账号 */
    private String username;

    /** 状态：0-禁用 1-正常 */
    private Integer status;

    /** 状态描述 */
    private String statusText;

    /** 角色：0-普通用户 1-管理员 */
    private Integer role;

    /** 创建时间 */
    private Date createTime;
}

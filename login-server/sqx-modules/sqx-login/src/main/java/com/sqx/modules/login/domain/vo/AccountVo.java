package com.sqx.modules.login.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 账号信息响应
 */
@Data
public class AccountVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 账号ID */
    private Long id;

    /** 登录账号 */
    private String username;

    /** 账号状态：0-禁用 1-正常 */
    private Integer status;

    /** 账号状态描述 */
    private String statusText;

    /** 角色：0-普通用户 1-管理员 */
    private Integer role;

    /** 创建时间 */
    private Date createTime;
}

package com.sqx.modules.login.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 账号信息响应
 */
@Data
public class AccountVo {

    /** 账号ID */
    private Long id;

    /** 登录账号 */
    private String account;

    /** 账号状态：1-正常，0-禁用 */
    private Integer status;

    /** 状态描述 */
    private String statusText;

    /** 创建时间 */
    private LocalDateTime createTime;
}

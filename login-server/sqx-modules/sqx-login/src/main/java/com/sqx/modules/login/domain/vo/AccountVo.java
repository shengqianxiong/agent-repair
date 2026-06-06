package com.sqx.modules.login.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * 账号信息视图
 */
@Data
public class AccountVo {

    private Long id;
    private String username;
    /** 账号状态：0-禁用，1-正常 */
    private Integer status;
    private Date createTime;
}

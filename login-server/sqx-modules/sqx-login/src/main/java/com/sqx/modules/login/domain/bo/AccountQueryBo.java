package com.sqx.modules.login.domain.bo;

import lombok.Data;

/**
 * 账号列表查询条件
 */
@Data
public class AccountQueryBo {

    /** 账号关键词（模糊匹配） */
    private String username;

    private Integer page = 1;
    private Integer pageSize = 10;
}

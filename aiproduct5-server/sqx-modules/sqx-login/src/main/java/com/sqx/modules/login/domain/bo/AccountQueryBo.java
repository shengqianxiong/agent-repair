package com.sqx.modules.login.domain.bo;

import lombok.Data;

/**
 * 账号列表查询参数
 */
@Data
public class AccountQueryBo {

    /** 账号关键词（模糊搜索） */
    private String account;

    /** 页码，从 1 开始 */
    private Integer page = 1;

    /** 每页条数 */
    private Integer pageSize = 10;
}

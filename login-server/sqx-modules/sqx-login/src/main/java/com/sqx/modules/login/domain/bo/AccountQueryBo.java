package com.sqx.modules.login.domain.bo;

import lombok.Data;

/**
 * 账号列表查询参数
 */
@Data
public class AccountQueryBo {

    /** 账号关键词（模糊查询） */
    private String username;

    /** 页码，默认 1 */
    private Integer page = 1;

    /** 每页条数，默认 10 */
    private Integer pageSize = 10;
}

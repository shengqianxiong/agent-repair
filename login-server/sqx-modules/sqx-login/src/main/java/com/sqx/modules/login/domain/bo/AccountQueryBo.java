package com.sqx.modules.login.domain.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * 账号列表查询参数
 */
@Data
public class AccountQueryBo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 页码，从 1 开始 */
    private Integer page;

    /** 每页条数 */
    private Integer pageSize;

    /** 账号关键词（模糊匹配） */
    private String keyword;
}

package com.sqx.modules.login.domain.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * 编辑账号请求参数（不可修改账号名称）。
 */
@Data
public class AccountUpdateBo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 账号主键 */
    private Long id;

    /** 重置密码（明文，可选） */
    private String password;

    /** 账号状态：1-正常，0-禁用 */
    private Integer status;
}

package com.sqx.modules.login.domain.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * 编辑账号请求（不可修改账号名）
 */
@Data
public class AccountUpdateBo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 账号ID */
    private Long id;

    /** 重置密码（可选） */
    private String password;

    /** 账号状态：0-禁用 1-正常 */
    private Integer status;
}

package com.sqx.modules.login.domain.bo;

import lombok.Data;

/**
 * 编辑账号参数
 */
@Data
public class AccountUpdateBo {

    /** 账号ID */
    private Long id;

    /** 重置密码（留空则不修改） */
    private String password;

    /** 账号状态：1-正常，0-禁用 */
    private Integer status;
}

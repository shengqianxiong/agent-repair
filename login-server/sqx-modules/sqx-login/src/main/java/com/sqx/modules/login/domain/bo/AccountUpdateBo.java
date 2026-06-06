package com.sqx.modules.login.domain.bo;

import lombok.Data;

/**
 * 编辑账号请求参数
 */
@Data
public class AccountUpdateBo {

    /** 账号ID */
    private Long id;

    /** 重置密码（可选） */
    private String password;

    /** 账号状态：0-禁用，1-正常 */
    private Integer status;
}

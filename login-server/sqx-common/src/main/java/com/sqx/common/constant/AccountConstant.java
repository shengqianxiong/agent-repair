package com.sqx.common.constant;

/**
 * 账号相关常量
 */
public final class AccountConstant {

    /** 账号状态：正常 */
    public static final int STATUS_NORMAL = 1;

    /** 账号状态：禁用 */
    public static final int STATUS_DISABLED = 0;

    /** 角色：管理员 */
    public static final int ROLE_ADMIN = 1;

    /** 角色：普通用户 */
    public static final int ROLE_USER = 0;

    /** Token 默认有效期（小时） */
    public static final int TOKEN_EXPIRE_HOURS = 2;

    private AccountConstant() {
    }
}

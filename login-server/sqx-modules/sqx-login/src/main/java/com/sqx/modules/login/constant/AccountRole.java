package com.sqx.modules.login.constant;

/**
 * 账号角色常量
 */
public final class AccountRole {

    /** 普通用户 */
    public static final int USER = 0;

    /** 管理员 */
    public static final int ADMIN = 1;

    private AccountRole() {
    }

    /**
     * 判断是否为管理员
     *
     * @param role 角色值
     * @return 是否管理员
     */
    public static boolean isAdmin(Integer role) {
        return role != null && role == ADMIN;
    }
}

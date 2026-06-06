package com.sqx.common.utils;

/**
 * 当前登录管理员上下文（Admin 端）
 */
public final class AdminContext {

    private static final ThreadLocal<Long> ACCOUNT_ID = new ThreadLocal<>();

    private AdminContext() {
    }

    public static void setAccountId(Long accountId) {
        ACCOUNT_ID.set(accountId);
    }

    public static Long getAccountId() {
        return ACCOUNT_ID.get();
    }

    public static void clear() {
        ACCOUNT_ID.remove();
    }
}

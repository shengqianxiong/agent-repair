package com.sqx.common.utils;

/**
 * 当前登录用户上下文（App 端）
 */
public final class UserContext {

    private static final ThreadLocal<Long> ACCOUNT_ID = new ThreadLocal<>();

    private UserContext() {
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

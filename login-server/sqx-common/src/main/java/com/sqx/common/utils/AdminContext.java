package com.sqx.common.utils;

/**
 * 管理端当前登录账号上下文（ThreadLocal）。
 */
public final class AdminContext {

    private static final ThreadLocal<Long> ACCOUNT_ID = new ThreadLocal<>();

    private AdminContext() {
    }

    /**
     * 设置当前管理员账号 ID。
     *
     * @param accountId 账号主键
     */
    public static void setAccountId(Long accountId) {
        ACCOUNT_ID.set(accountId);
    }

    /**
     * 获取当前管理员账号 ID。
     *
     * @return 账号主键，未登录时返回 null
     */
    public static Long getAccountId() {
        return ACCOUNT_ID.get();
    }

    /**
     * 清理线程上下文，防止线程复用导致数据串扰。
     */
    public static void clear() {
        ACCOUNT_ID.remove();
    }
}

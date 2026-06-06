package com.sqx.common.utils;

/**
 * 当前登录管理员上下文（管理端）
 */
public final class AdminContext {

    private static final ThreadLocal<Long> ADMIN_ID = new ThreadLocal<>();

    private AdminContext() {
    }

    public static void setAdminId(Long adminId) {
        ADMIN_ID.set(adminId);
    }

    public static Long getAdminId() {
        return ADMIN_ID.get();
    }

    public static void clear() {
        ADMIN_ID.remove();
    }
}

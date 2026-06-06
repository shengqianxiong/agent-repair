package com.sqx.common.utils;

/**
 * 登录 Token 生成工具
 */
public final class TokenUtils {

    private static final String ADMIN_TOKEN_PREFIX = "admin:";
    private static final String APP_TOKEN_PREFIX = "uid:";

    private TokenUtils() {
    }

    /**
     * 生成管理端登录 Token
     *
     * @param adminId 管理员 ID
     * @return Token 字符串
     */
    public static String generateAdminToken(Long adminId) {
        return ADMIN_TOKEN_PREFIX + adminId;
    }

    /**
     * 生成用户端登录 Token
     *
     * @param userId 用户 ID
     * @return Token 字符串
     */
    public static String generateAppToken(Long userId) {
        return APP_TOKEN_PREFIX + userId;
    }
}

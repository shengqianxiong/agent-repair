package com.sqx.common.utils;

/**
 * Token 生成与解析工具
 */
public final class TokenUtils {

    private static final String ADMIN_PREFIX = "admin:";
    private static final String APP_PREFIX = "uid:";

    private TokenUtils() {
    }

    /**
     * 生成管理端登录 Token
     *
     * @param adminId 管理员 ID
     * @return Token 字符串
     */
    public static String generateAdminToken(Long adminId) {
        return ADMIN_PREFIX + adminId;
    }

    /**
     * 生成用户端登录 Token
     *
     * @param userId 用户 ID
     * @return Token 字符串
     */
    public static String generateAppToken(Long userId) {
        return APP_PREFIX + userId;
    }
}

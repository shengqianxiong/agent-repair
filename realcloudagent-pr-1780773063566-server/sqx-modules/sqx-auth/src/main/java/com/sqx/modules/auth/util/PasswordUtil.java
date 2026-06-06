package com.sqx.modules.auth.util;

import cn.hutool.crypto.digest.DigestUtil;

/**
 * 密码工具类，使用 MD5 进行摘要存储与校验。
 */
public final class PasswordUtil {

    private PasswordUtil() {
    }

    /**
     * 对明文密码进行 MD5 摘要。
     *
     * @param rawPassword 明文密码
     * @return 摘要后的密码
     */
    public static String encrypt(String rawPassword) {
        return DigestUtil.md5Hex(rawPassword);
    }

    /**
     * 校验明文密码与摘要是否匹配。
     *
     * @param rawPassword     明文密码
     * @param encodedPassword 摘要密码
     * @return 是否匹配
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return encrypt(rawPassword).equals(encodedPassword);
    }
}

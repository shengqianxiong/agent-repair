package com.sqx.common.utils;

import cn.hutool.crypto.digest.DigestUtil;

/**
 * 密码加密工具
 */
public final class PasswordUtils {

    private PasswordUtils() {
    }

    /**
     * 对明文密码进行 MD5 加密
     *
     * @param rawPassword 明文密码
     * @return 加密后的密码
     */
    public static String encrypt(String rawPassword) {
        return DigestUtil.md5Hex(rawPassword);
    }

    /**
     * 校验明文密码与密文是否匹配
     *
     * @param rawPassword     明文密码
     * @param encodedPassword 密文密码
     * @return 是否匹配
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return encrypt(rawPassword).equals(encodedPassword);
    }
}

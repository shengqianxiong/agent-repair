package com.sqx.common.utils;

import cn.hutool.crypto.digest.DigestUtil;

/**
 * 密码加密工具，统一使用 MD5 存储。
 */
public final class PasswordUtils {

    private PasswordUtils() {
    }

    /**
     * 将明文密码加密为 MD5 十六进制字符串。
     *
     * @param plainPassword 明文密码
     * @return MD5 密文
     */
    public static String encrypt(String plainPassword) {
        return DigestUtil.md5Hex(plainPassword);
    }

    /**
     * 校验明文密码与密文是否匹配。
     *
     * @param plainPassword 明文密码
     * @param encryptedPassword 已加密密码
     * @return 是否匹配
     */
    public static boolean matches(String plainPassword, String encryptedPassword) {
        return encrypt(plainPassword).equals(encryptedPassword);
    }
}

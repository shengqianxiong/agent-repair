package com.sqx.modules.login.util;

import cn.hutool.crypto.digest.DigestUtil;

/**
 * 密码工具类，统一使用 MD5 加密存储
 */
public final class PasswordUtil {

    private PasswordUtil() {
    }

    /**
     * 对明文密码进行 MD5 加密
     *
     * @param plainPassword 明文密码
     * @return MD5 密文
     */
    public static String encrypt(String plainPassword) {
        return DigestUtil.md5Hex(plainPassword);
    }

    /**
     * 校验明文密码与密文是否匹配
     *
     * @param plainPassword     明文密码
     * @param encryptedPassword 密文密码
     * @return 是否匹配
     */
    public static boolean matches(String plainPassword, String encryptedPassword) {
        return encrypt(plainPassword).equals(encryptedPassword);
    }
}

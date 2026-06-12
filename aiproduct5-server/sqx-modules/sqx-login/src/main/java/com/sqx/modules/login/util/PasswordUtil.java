package com.sqx.modules.login.util;

import cn.hutool.crypto.digest.DigestUtil;

/**
 * 密码工具类（MD5 加密）
 */
public final class PasswordUtil {

    private PasswordUtil() {
    }

    /**
     * 对明文密码进行 MD5 加密
     */
    public static String encrypt(String plainPassword) {
        return DigestUtil.md5Hex(plainPassword);
    }

    /**
     * 校验明文密码与加密密码是否匹配
     */
    public static boolean matches(String plainPassword, String encryptedPassword) {
        return encrypt(plainPassword).equals(encryptedPassword);
    }
}

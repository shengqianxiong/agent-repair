package com.sqx.modules.login.util;

import cn.hutool.crypto.digest.DigestUtil;

/**
 * 密码工具类
 */
public final class PasswordUtil {

    private PasswordUtil() {
    }

    /**
     * 对明文密码进行 MD5 加密
     *
     * @param rawPassword 明文密码
     * @return MD5 密文
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

package com.sqx.common.utils;

import cn.hutool.crypto.digest.DigestUtil;

/**
 * 密码工具类，使用 MD5 摘要校验
 */
public final class PasswordUtils {

    private PasswordUtils() {
    }

    /**
     * 对明文密码进行 MD5 加密
     *
     * @param rawPassword 明文密码
     * @return MD5 摘要
     */
    public static String encrypt(String rawPassword) {
        return DigestUtil.md5Hex(rawPassword);
    }

    /**
     * 校验明文密码与密文是否匹配
     *
     * @param rawPassword     明文密码
     * @param encodedPassword 数据库存储的 MD5 密文
     * @return 是否匹配
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return encrypt(rawPassword).equals(encodedPassword);
    }
}

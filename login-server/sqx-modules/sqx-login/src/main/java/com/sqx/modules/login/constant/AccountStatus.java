package com.sqx.modules.login.constant;

/**
 * 账号状态常量
 */
public final class AccountStatus {

    /** 禁用 */
    public static final int DISABLED = 0;

    /** 正常 */
    public static final int NORMAL = 1;

    private AccountStatus() {
    }

    /**
     * 判断账号是否可用
     *
     * @param status 状态值
     * @return 是否正常
     */
    public static boolean isNormal(Integer status) {
        return status != null && status == NORMAL;
    }
}

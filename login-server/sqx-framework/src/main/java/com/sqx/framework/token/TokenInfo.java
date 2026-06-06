package com.sqx.framework.token;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 会话 Token 信息
 */
@Data
@AllArgsConstructor
public class TokenInfo {

    /** 账号ID */
    private Long accountId;

    /** 角色：0-普通用户 1-管理员 */
    private Integer role;

    /** 过期时间戳（毫秒） */
    private long expireAt;
}

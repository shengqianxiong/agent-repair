package com.sqx.modules.login.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录 Token 实体
 */
@Data
@TableName("login_token")
public class LoginToken {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 账号ID */
    private Long accountId;

    /** 会话 Token */
    private String token;

    /** 过期时间 */
    private LocalDateTime expireTime;

    /** 创建时间 */
    private LocalDateTime createTime;
}

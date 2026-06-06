package com.sqx.modules.login.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 登录会话 Token 实体
 */
@Data
@TableName("login_token")
public class LoginToken implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联账号 ID */
    private Long accountId;

    /** 会话 Token */
    private String token;

    /** 过期时间 */
    private Date expireTime;

    private Date createTime;
}

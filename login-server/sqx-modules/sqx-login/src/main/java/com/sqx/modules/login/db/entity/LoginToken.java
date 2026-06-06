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

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 会话Token */
    private String token;

    /** 关联账号ID */
    private Long accountId;

    /** 过期时间 */
    private Date expireTime;

    /** 创建时间 */
    private Date createTime;
}

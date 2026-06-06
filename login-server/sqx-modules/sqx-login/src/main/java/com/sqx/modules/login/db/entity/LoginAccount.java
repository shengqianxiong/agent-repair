package com.sqx.modules.login.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录账号实体
 */
@Data
@TableName("login_account")
public class LoginAccount {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 登录账号 */
    private String account;

    /** 密码（MD5） */
    private String password;

    /** 账号状态：1-正常，0-禁用 */
    private Integer status;

    /** 角色：1-管理员，0-普通用户 */
    private Integer role;

    /** 创建时间 */
    private LocalDateTime createTime;
}

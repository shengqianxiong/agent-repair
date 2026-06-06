package com.sqx.modules.login.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 登录账号实体
 */
@Data
@TableName("login_account")
public class LoginAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 登录账号 */
    private String username;

    /** 密码（MD5 加密） */
    private String password;

    /** 账号状态：0-禁用，1-正常 */
    private Integer status;

    private Date createTime;
    private Date updateTime;
}

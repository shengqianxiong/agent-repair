package com.sqx.modules.login.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统登录账号实体。
 */
@Data
@TableName("login_account")
public class LoginAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 登录账号，全局唯一 */
    private String username;

    /** 密码（MD5 加密存储） */
    private String password;

    /** 账号状态：1-正常，0-禁用 */
    private Integer status;

    /** 角色：1-管理员，0-普通用户 */
    private Integer role;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;
}

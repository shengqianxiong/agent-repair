package com.sqx.modules.agent.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 项目客户
 */
@Data
@TableName("agent_customer")
public class AgentCustomer implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long projectId;
    private String name;
    private String contact;
    private String remark;
    /** 0-正常 1-已删除 */
    private Integer deleted;
    private Date deletedTime;
    private Date createTime;
    private Date updateTime;
}

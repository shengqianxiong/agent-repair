package com.sqx.modules.agent.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 客户交付物
 */
@Data
@TableName("agent_delivery")
public class AgentDelivery implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long projectId;
    private Long customerId;
    private String name;
    /** 交付物目录 */
    private String directoryPath;
    /** 0-草稿 1-已交付 */
    private Integer status;
    /** 0-正常 1-已删除 */
    private Integer deleted;
    private Date deletedTime;
    private Date createTime;
    private Date updateTime;
}

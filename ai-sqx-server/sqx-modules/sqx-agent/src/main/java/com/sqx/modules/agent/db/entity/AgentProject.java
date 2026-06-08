package com.sqx.modules.agent.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 自动化项目
 */
@Data
@TableName("agent_project")
public class AgentProject implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String code;
    /** 项目工作区目录 */
    private String directoryPath;
    private String description;
    /** 0-正常 1-已删除 */
    private Integer deleted;
    private Date deletedTime;
    private Date createTime;
    private Date updateTime;
}

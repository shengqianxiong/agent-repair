package com.sqx.modules.agent.domain.vo;

import lombok.Data;

import java.util.Date;

@Data
public class AgentCustomerVo {

    private Long id;
    private Long projectId;
    private String projectName;
    private String name;
    private String contact;
    private String remark;
    private Date deletedTime;
    private Date createTime;
    private Date updateTime;
}

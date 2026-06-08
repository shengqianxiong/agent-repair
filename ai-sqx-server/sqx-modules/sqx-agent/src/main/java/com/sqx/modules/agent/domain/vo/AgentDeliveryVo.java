package com.sqx.modules.agent.domain.vo;

import lombok.Data;

import java.util.Date;

@Data
public class AgentDeliveryVo {

    private Long id;
    private Long projectId;
    private String projectName;
    private Long customerId;
    private String customerName;
    private String name;
    private String directoryPath;
    private Integer status;
    private Date deletedTime;
    private Date createTime;
    private Date updateTime;
}

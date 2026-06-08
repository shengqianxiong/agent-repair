package com.sqx.modules.agent.domain.vo;

import lombok.Data;

import java.util.Date;

@Data
public class AgentProjectVo {

    private Long id;
    private String name;
    private String code;
    private String directoryPath;
    private String description;
    private Date deletedTime;
    private Date createTime;
    private Date updateTime;
}

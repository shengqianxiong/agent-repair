package com.sqx.modules.agent.domain.bo;

import lombok.Data;

@Data
public class AgentProjectSaveBo {

    private Long id;
    private String name;
    private String code;
    private String directoryPath;
    private String description;
}

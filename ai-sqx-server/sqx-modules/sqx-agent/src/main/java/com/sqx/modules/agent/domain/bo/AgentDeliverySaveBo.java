package com.sqx.modules.agent.domain.bo;

import lombok.Data;

@Data
public class AgentDeliverySaveBo {

    private Long id;
    private Long projectId;
    private Long customerId;
    private String name;
    private String directoryPath;
    private Integer status;
}

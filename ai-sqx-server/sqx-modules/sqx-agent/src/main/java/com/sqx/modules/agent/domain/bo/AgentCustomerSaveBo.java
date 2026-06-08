package com.sqx.modules.agent.domain.bo;

import lombok.Data;

@Data
public class AgentCustomerSaveBo {

    private Long id;
    private Long projectId;
    private String name;
    private String contact;
    private String remark;
}

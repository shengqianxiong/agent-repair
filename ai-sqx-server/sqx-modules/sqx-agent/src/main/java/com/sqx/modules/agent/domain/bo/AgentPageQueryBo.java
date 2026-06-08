package com.sqx.modules.agent.domain.bo;

import lombok.Data;

/**
 * 通用分页查询
 */
@Data
public class AgentPageQueryBo {

    private Integer page;
    private Integer pageSize;
    private String name;
    private Long projectId;
    private Long customerId;
}

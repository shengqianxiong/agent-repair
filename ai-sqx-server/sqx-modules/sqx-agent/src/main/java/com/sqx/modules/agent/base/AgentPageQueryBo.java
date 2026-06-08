package com.sqx.modules.agent.base;

import lombok.Data;

/**
 * Agent 模块通用分页查询
 */
@Data
public class AgentPageQueryBo {

    private Integer page;
    private Integer pageSize;
    private String keyword;
    private Integer status;
}

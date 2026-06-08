package com.sqx.modules.agent.service;

import com.sqx.common.PageResult;
import com.sqx.modules.agent.domain.bo.AgentCustomerSaveBo;
import com.sqx.modules.agent.domain.bo.AgentPageQueryBo;
import com.sqx.modules.agent.domain.vo.AgentCustomerVo;

public interface AgentCustomerService {

    PageResult<AgentCustomerVo> page(AgentPageQueryBo queryBo);

    AgentCustomerVo detail(Long id);

    void save(AgentCustomerSaveBo bo);

    void update(AgentCustomerSaveBo bo);

    void softDelete(Long id);

    PageResult<AgentCustomerVo> recyclePage(AgentPageQueryBo queryBo);

    void restore(Long id);

    void permanentDelete(Long id);
}

package com.sqx.modules.agent.service;

import com.sqx.common.PageResult;
import com.sqx.modules.agent.domain.bo.AgentDeliverySaveBo;
import com.sqx.modules.agent.domain.bo.AgentPageQueryBo;
import com.sqx.modules.agent.domain.vo.AgentDeliveryVo;

public interface AgentDeliveryService {

    PageResult<AgentDeliveryVo> page(AgentPageQueryBo queryBo);

    AgentDeliveryVo detail(Long id);

    void save(AgentDeliverySaveBo bo);

    void update(AgentDeliverySaveBo bo);

    void softDelete(Long id);

    PageResult<AgentDeliveryVo> recyclePage(AgentPageQueryBo queryBo);

    void restore(Long id);

    void permanentDelete(Long id);
}

package com.sqx.modules.agent.service;

import com.sqx.common.PageResult;
import com.sqx.modules.agent.domain.bo.AgentPageQueryBo;
import com.sqx.modules.agent.domain.bo.AgentProjectSaveBo;
import com.sqx.modules.agent.domain.vo.AgentProjectVo;

public interface AgentProjectService {

    PageResult<AgentProjectVo> page(AgentPageQueryBo queryBo);

    AgentProjectVo detail(Long id);

    void save(AgentProjectSaveBo bo);

    void update(AgentProjectSaveBo bo);

    void softDelete(Long id);

    PageResult<AgentProjectVo> recyclePage(AgentPageQueryBo queryBo);

    void restore(Long id);

    void permanentDelete(Long id);
}

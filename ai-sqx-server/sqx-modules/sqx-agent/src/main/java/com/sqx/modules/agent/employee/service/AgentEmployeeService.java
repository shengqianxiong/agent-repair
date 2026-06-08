package com.sqx.modules.agent.employee.service;

import com.sqx.common.PageResult;
import com.sqx.modules.agent.base.AgentPageQueryBo;
import com.sqx.modules.agent.employee.db.entity.AgentEmployee;
import com.sqx.modules.agent.employee.domain.bo.EmployeeSaveBo;

public interface AgentEmployeeService {

    PageResult<AgentEmployee> page(AgentPageQueryBo queryBo);

    AgentEmployee detail(Long id);

    void save(EmployeeSaveBo bo);

    void update(EmployeeSaveBo bo);

    void delete(Long id);

    PageResult<AgentEmployee> recyclePage(AgentPageQueryBo queryBo);

    void restore(Long id);

    void permanentDelete(Long id);
}

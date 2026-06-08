package com.sqx.modules.agent.employee.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.common.PageResult;
import com.sqx.common.exception.BusinessException;
import com.sqx.modules.agent.base.AbstractAgentService;
import com.sqx.modules.agent.base.AgentPageQueryBo;
import com.sqx.modules.agent.base.RecycleBinSupport;
import com.sqx.modules.agent.employee.db.entity.AgentEmployee;
import com.sqx.modules.agent.employee.db.mapper.AgentEmployeeMapper;
import com.sqx.modules.agent.employee.domain.bo.EmployeeSaveBo;
import com.sqx.modules.agent.employee.service.AgentEmployeeService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AgentEmployeeServiceImpl extends AbstractAgentService<AgentEmployee, AgentEmployeeMapper>
        implements AgentEmployeeService {

    public AgentEmployeeServiceImpl(AgentEmployeeMapper mapper, RecycleBinSupport recycleBinSupport) {
        super(mapper, recycleBinSupport);
    }

    @Override
    protected String tableName() {
        return "agent_employee";
    }

    @Override
    protected Class<AgentEmployee> entityClass() {
        return AgentEmployee.class;
    }

    @Override
    protected String entityLabel() {
        return "员工";
    }

    @Override
    protected com.baomidou.mybatisplus.core.toolkit.support.SFunction<AgentEmployee, Date> createTimeColumn() {
        return AgentEmployee::getCreateTime;
    }

    @Override
    public PageResult<AgentEmployee> page(AgentPageQueryBo queryBo) {
        LambdaQueryWrapper<AgentEmployee> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(queryBo.getKeyword())) {
            wrapper.and(w -> w.like(AgentEmployee::getName, queryBo.getKeyword())
                    .or().like(AgentEmployee::getPhone, queryBo.getKeyword()));
        }
        if (queryBo.getStatus() != null) {
            wrapper.eq(AgentEmployee::getStatus, queryBo.getStatus());
        }
        return pageQuery(queryBo, wrapper);
    }

    @Override
    public AgentEmployee detail(Long id) {
        return getOrThrow(id);
    }

    @Override
    public void save(EmployeeSaveBo bo) {
        validateSaveBo(bo, false);
        AgentEmployee entity = new AgentEmployee();
        BeanUtil.copyProperties(bo, entity);
        entity.setStatus(bo.getStatus() == null ? 1 : bo.getStatus());
        fillCreateTime(entity);
        mapper.insert(entity);
    }

    @Override
    public void update(EmployeeSaveBo bo) {
        if (bo.getId() == null) {
            throw new BusinessException("员工ID不能为空");
        }
        validateSaveBo(bo, true);
        AgentEmployee entity = getOrThrow(bo.getId());
        entity.setName(bo.getName());
        entity.setPhone(bo.getPhone());
        entity.setPosition(bo.getPosition());
        entity.setDepartment(bo.getDepartment());
        entity.setAvatar(bo.getAvatar());
        if (bo.getStatus() != null) {
            entity.setStatus(bo.getStatus());
        }
        touchUpdateTime(entity);
        mapper.updateById(entity);
    }

    @Override
    public void delete(Long id) {
        softDelete(id);
    }

    private void validateSaveBo(EmployeeSaveBo bo, boolean isUpdate) {
        if (!isUpdate && bo.getId() != null) {
            throw new BusinessException("新增员工不能携带ID");
        }
        if (StrUtil.isBlank(bo.getName())) {
            throw new BusinessException("员工姓名不能为空");
        }
    }
}

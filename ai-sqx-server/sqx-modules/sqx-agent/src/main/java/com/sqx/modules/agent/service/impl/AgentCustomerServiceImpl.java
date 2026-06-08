package com.sqx.modules.agent.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.common.PageResult;
import com.sqx.common.exception.BusinessException;
import com.sqx.modules.agent.db.entity.AgentCustomer;
import com.sqx.modules.agent.db.entity.AgentProject;
import com.sqx.modules.agent.db.mapper.AgentCustomerMapper;
import com.sqx.modules.agent.db.mapper.AgentDeliveryMapper;
import com.sqx.modules.agent.db.mapper.AgentProjectMapper;
import com.sqx.modules.agent.domain.bo.AgentCustomerSaveBo;
import com.sqx.modules.agent.domain.bo.AgentPageQueryBo;
import com.sqx.modules.agent.domain.vo.AgentCustomerVo;
import com.sqx.modules.agent.service.AgentCustomerService;
import com.sqx.modules.agent.support.AgentDeleteConstants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AgentCustomerServiceImpl implements AgentCustomerService {

    private final AgentCustomerMapper customerMapper;
    private final AgentProjectMapper projectMapper;
    private final AgentDeliveryMapper deliveryMapper;

    public AgentCustomerServiceImpl(AgentCustomerMapper customerMapper,
                                    AgentProjectMapper projectMapper,
                                    AgentDeliveryMapper deliveryMapper) {
        this.customerMapper = customerMapper;
        this.projectMapper = projectMapper;
        this.deliveryMapper = deliveryMapper;
    }

    @Override
    public PageResult<AgentCustomerVo> page(AgentPageQueryBo queryBo) {
        int page = resolvePage(queryBo.getPage());
        int pageSize = resolvePageSize(queryBo.getPageSize());

        LambdaQueryWrapper<AgentCustomer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgentCustomer::getDeleted, AgentDeleteConstants.NOT_DELETED);
        if (queryBo.getProjectId() != null) {
            wrapper.eq(AgentCustomer::getProjectId, queryBo.getProjectId());
        }
        if (StrUtil.isNotBlank(queryBo.getName())) {
            wrapper.like(AgentCustomer::getName, queryBo.getName());
        }
        wrapper.orderByDesc(AgentCustomer::getCreateTime);

        Page<AgentCustomer> pageData = customerMapper.selectPage(new Page<>(page, pageSize), wrapper);
        Map<Long, String> projectNameMap = loadProjectNameMap(pageData.getRecords());
        List<AgentCustomerVo> list = pageData.getRecords().stream()
                .map(customer -> toVo(customer, projectNameMap.get(customer.getProjectId())))
                .collect(Collectors.toList());
        return PageResult.of(list, pageData.getTotal(), page, pageSize);
    }

    @Override
    public AgentCustomerVo detail(Long id) {
        AgentCustomer customer = getActiveCustomerOrThrow(id);
        AgentProject project = projectMapper.selectById(customer.getProjectId());
        return toVo(customer, project == null ? null : project.getName());
    }

    @Override
    public void save(AgentCustomerSaveBo bo) {
        validateSaveBo(bo, false);
        getActiveProjectOrThrow(bo.getProjectId());
        Date now = new Date();
        AgentCustomer customer = new AgentCustomer();
        BeanUtil.copyProperties(bo, customer);
        customer.setDeleted(AgentDeleteConstants.NOT_DELETED);
        customer.setCreateTime(now);
        customer.setUpdateTime(now);
        customerMapper.insert(customer);
    }

    @Override
    public void update(AgentCustomerSaveBo bo) {
        if (bo.getId() == null) {
            throw new BusinessException("客户ID不能为空");
        }
        validateSaveBo(bo, true);
        AgentCustomer customer = getActiveCustomerOrThrow(bo.getId());
        if (bo.getProjectId() != null && !bo.getProjectId().equals(customer.getProjectId())) {
            getActiveProjectOrThrow(bo.getProjectId());
            customer.setProjectId(bo.getProjectId());
        }
        customer.setName(bo.getName());
        customer.setContact(bo.getContact());
        customer.setRemark(bo.getRemark());
        customer.setUpdateTime(new Date());
        customerMapper.updateById(customer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void softDelete(Long id) {
        getActiveCustomerOrThrow(id);
        Date now = new Date();
        int affected = customerMapper.softDeleteById(id, now);
        if (affected == 0) {
            throw new BusinessException("客户不存在或已删除");
        }
        deliveryMapper.softDeleteByCustomerId(id, now);
    }

    @Override
    public PageResult<AgentCustomerVo> recyclePage(AgentPageQueryBo queryBo) {
        int page = resolvePage(queryBo.getPage());
        int pageSize = resolvePageSize(queryBo.getPageSize());
        Page<AgentCustomer> pageData = customerMapper.selectRecyclePage(new Page<>(page, pageSize));
        Map<Long, String> projectNameMap = loadProjectNameMap(pageData.getRecords());
        List<AgentCustomerVo> list = pageData.getRecords().stream()
                .map(customer -> toVo(customer, projectNameMap.get(customer.getProjectId())))
                .collect(Collectors.toList());
        return PageResult.of(list, pageData.getTotal(), page, pageSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void restore(Long id) {
        AgentCustomer customer = getDeletedCustomerOrThrow(id);
        AgentProject project = projectMapper.selectById(customer.getProjectId());
        if (project == null || project.getDeleted() == null || project.getDeleted() != AgentDeleteConstants.NOT_DELETED) {
            throw new BusinessException("所属项目不存在或已删除，无法恢复客户");
        }
        Date now = new Date();
        int affected = customerMapper.restoreById(id, now);
        if (affected == 0) {
            throw new BusinessException("客户恢复失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void permanentDelete(Long id) {
        getDeletedCustomerOrThrow(id);
        int affected = customerMapper.permanentDeleteById(id);
        if (affected == 0) {
            throw new BusinessException("客户彻底删除失败");
        }
    }

    private AgentCustomer getActiveCustomerOrThrow(Long id) {
        AgentCustomer customer = customerMapper.selectById(id);
        if (customer == null || customer.getDeleted() == null || customer.getDeleted() != AgentDeleteConstants.NOT_DELETED) {
            throw new BusinessException("客户不存在");
        }
        return customer;
    }

    private AgentCustomer getDeletedCustomerOrThrow(Long id) {
        AgentCustomer customer = customerMapper.selectByIdIncludeDeleted(id);
        if (customer == null || customer.getDeleted() == null || customer.getDeleted() != AgentDeleteConstants.DELETED) {
            throw new BusinessException("回收站中不存在该客户");
        }
        return customer;
    }

    private AgentProject getActiveProjectOrThrow(Long projectId) {
        AgentProject project = projectMapper.selectById(projectId);
        if (project == null || project.getDeleted() == null || project.getDeleted() != AgentDeleteConstants.NOT_DELETED) {
            throw new BusinessException("所属项目不存在");
        }
        return project;
    }

    private void validateSaveBo(AgentCustomerSaveBo bo, boolean isUpdate) {
        if (!isUpdate && bo.getId() != null) {
            throw new BusinessException("新增客户不能携带ID");
        }
        if (bo.getProjectId() == null) {
            throw new BusinessException("所属项目不能为空");
        }
        if (StrUtil.isBlank(bo.getName())) {
            throw new BusinessException("客户名称不能为空");
        }
    }

    private Map<Long, String> loadProjectNameMap(List<AgentCustomer> customers) {
        return customers.stream()
                .map(AgentCustomer::getProjectId)
                .distinct()
                .collect(Collectors.toMap(projectId -> projectId, projectId -> {
                    AgentProject project = projectMapper.selectById(projectId);
                    return project == null ? null : project.getName();
                }, (a, b) -> a));
    }

    private AgentCustomerVo toVo(AgentCustomer customer, String projectName) {
        AgentCustomerVo vo = new AgentCustomerVo();
        BeanUtil.copyProperties(customer, vo);
        vo.setProjectName(projectName);
        return vo;
    }

    private int resolvePage(Integer page) {
        return page == null || page < 1 ? 1 : page;
    }

    private int resolvePageSize(Integer pageSize) {
        return pageSize == null || pageSize < 1 ? 10 : pageSize;
    }
}

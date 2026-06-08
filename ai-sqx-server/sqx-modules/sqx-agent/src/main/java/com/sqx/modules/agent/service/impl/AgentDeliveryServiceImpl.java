package com.sqx.modules.agent.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.common.PageResult;
import com.sqx.common.exception.BusinessException;
import com.sqx.modules.agent.db.entity.AgentCustomer;
import com.sqx.modules.agent.db.entity.AgentDelivery;
import com.sqx.modules.agent.db.entity.AgentProject;
import com.sqx.modules.agent.db.mapper.AgentCustomerMapper;
import com.sqx.modules.agent.db.mapper.AgentDeliveryMapper;
import com.sqx.modules.agent.db.mapper.AgentProjectMapper;
import com.sqx.modules.agent.domain.bo.AgentDeliverySaveBo;
import com.sqx.modules.agent.domain.bo.AgentPageQueryBo;
import com.sqx.modules.agent.domain.vo.AgentDeliveryVo;
import com.sqx.modules.agent.service.AgentDeliveryService;
import com.sqx.modules.agent.support.AgentDeleteConstants;
import com.sqx.modules.agent.support.DirectoryChecker;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AgentDeliveryServiceImpl implements AgentDeliveryService {

    private final AgentDeliveryMapper deliveryMapper;
    private final AgentProjectMapper projectMapper;
    private final AgentCustomerMapper customerMapper;

    public AgentDeliveryServiceImpl(AgentDeliveryMapper deliveryMapper,
                                    AgentProjectMapper projectMapper,
                                    AgentCustomerMapper customerMapper) {
        this.deliveryMapper = deliveryMapper;
        this.projectMapper = projectMapper;
        this.customerMapper = customerMapper;
    }

    @Override
    public PageResult<AgentDeliveryVo> page(AgentPageQueryBo queryBo) {
        int page = resolvePage(queryBo.getPage());
        int pageSize = resolvePageSize(queryBo.getPageSize());

        LambdaQueryWrapper<AgentDelivery> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgentDelivery::getDeleted, AgentDeleteConstants.NOT_DELETED);
        if (queryBo.getProjectId() != null) {
            wrapper.eq(AgentDelivery::getProjectId, queryBo.getProjectId());
        }
        if (queryBo.getCustomerId() != null) {
            wrapper.eq(AgentDelivery::getCustomerId, queryBo.getCustomerId());
        }
        if (StrUtil.isNotBlank(queryBo.getName())) {
            wrapper.like(AgentDelivery::getName, queryBo.getName());
        }
        wrapper.orderByDesc(AgentDelivery::getCreateTime);

        Page<AgentDelivery> pageData = deliveryMapper.selectPage(new Page<>(page, pageSize), wrapper);
        Map<Long, String> projectNameMap = loadProjectNameMap(pageData.getRecords());
        Map<Long, String> customerNameMap = loadCustomerNameMap(pageData.getRecords());
        List<AgentDeliveryVo> list = pageData.getRecords().stream()
                .map(delivery -> toVo(delivery,
                        projectNameMap.get(delivery.getProjectId()),
                        customerNameMap.get(delivery.getCustomerId())))
                .collect(Collectors.toList());
        return PageResult.of(list, pageData.getTotal(), page, pageSize);
    }

    @Override
    public AgentDeliveryVo detail(Long id) {
        AgentDelivery delivery = getActiveDeliveryOrThrow(id);
        AgentProject project = projectMapper.selectById(delivery.getProjectId());
        AgentCustomer customer = customerMapper.selectById(delivery.getCustomerId());
        return toVo(delivery,
                project == null ? null : project.getName(),
                customer == null ? null : customer.getName());
    }

    @Override
    public void save(AgentDeliverySaveBo bo) {
        validateSaveBo(bo, false);
        getActiveProjectOrThrow(bo.getProjectId());
        getActiveCustomerOrThrow(bo.getCustomerId(), bo.getProjectId());
        Date now = new Date();
        AgentDelivery delivery = new AgentDelivery();
        BeanUtil.copyProperties(bo, delivery);
        delivery.setStatus(bo.getStatus() == null ? 0 : bo.getStatus());
        delivery.setDeleted(AgentDeleteConstants.NOT_DELETED);
        delivery.setCreateTime(now);
        delivery.setUpdateTime(now);
        deliveryMapper.insert(delivery);
    }

    @Override
    public void update(AgentDeliverySaveBo bo) {
        if (bo.getId() == null) {
            throw new BusinessException("交付ID不能为空");
        }
        validateSaveBo(bo, true);
        AgentDelivery delivery = getActiveDeliveryOrThrow(bo.getId());
        if (bo.getProjectId() != null && !bo.getProjectId().equals(delivery.getProjectId())) {
            getActiveProjectOrThrow(bo.getProjectId());
            delivery.setProjectId(bo.getProjectId());
        }
        if (bo.getCustomerId() != null && !bo.getCustomerId().equals(delivery.getCustomerId())) {
            getActiveCustomerOrThrow(bo.getCustomerId(), delivery.getProjectId());
            delivery.setCustomerId(bo.getCustomerId());
        }
        delivery.setName(bo.getName());
        delivery.setDirectoryPath(bo.getDirectoryPath());
        if (bo.getStatus() != null) {
            delivery.setStatus(bo.getStatus());
        }
        delivery.setUpdateTime(new Date());
        deliveryMapper.updateById(delivery);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void softDelete(Long id) {
        getActiveDeliveryOrThrow(id);
        Date now = new Date();
        int affected = deliveryMapper.softDeleteById(id, now);
        if (affected == 0) {
            throw new BusinessException("交付不存在或已删除");
        }
    }

    @Override
    public PageResult<AgentDeliveryVo> recyclePage(AgentPageQueryBo queryBo) {
        int page = resolvePage(queryBo.getPage());
        int pageSize = resolvePageSize(queryBo.getPageSize());
        Page<AgentDelivery> pageData = deliveryMapper.selectRecyclePage(new Page<>(page, pageSize));
        Map<Long, String> projectNameMap = loadProjectNameMap(pageData.getRecords());
        Map<Long, String> customerNameMap = loadCustomerNameMap(pageData.getRecords());
        List<AgentDeliveryVo> list = pageData.getRecords().stream()
                .map(delivery -> toVo(delivery,
                        projectNameMap.get(delivery.getProjectId()),
                        customerNameMap.get(delivery.getCustomerId())))
                .collect(Collectors.toList());
        return PageResult.of(list, pageData.getTotal(), page, pageSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void restore(Long id) {
        AgentDelivery delivery = getDeletedDeliveryOrThrow(id);
        if (StrUtil.isNotBlank(delivery.getDirectoryPath()) && !DirectoryChecker.exists(delivery.getDirectoryPath())) {
            throw new BusinessException("交付目录不存在，无法恢复");
        }
        AgentProject project = projectMapper.selectById(delivery.getProjectId());
        if (project == null || project.getDeleted() == null || project.getDeleted() != AgentDeleteConstants.NOT_DELETED) {
            throw new BusinessException("所属项目不存在或已删除，无法恢复交付");
        }
        AgentCustomer customer = customerMapper.selectById(delivery.getCustomerId());
        if (customer == null || customer.getDeleted() == null || customer.getDeleted() != AgentDeleteConstants.NOT_DELETED) {
            throw new BusinessException("所属客户不存在或已删除，无法恢复交付");
        }
        Date now = new Date();
        int affected = deliveryMapper.restoreById(id, now);
        if (affected == 0) {
            throw new BusinessException("交付恢复失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void permanentDelete(Long id) {
        getDeletedDeliveryOrThrow(id);
        int affected = deliveryMapper.permanentDeleteById(id);
        if (affected == 0) {
            throw new BusinessException("交付彻底删除失败");
        }
    }

    private AgentDelivery getActiveDeliveryOrThrow(Long id) {
        AgentDelivery delivery = deliveryMapper.selectById(id);
        if (delivery == null || delivery.getDeleted() == null || delivery.getDeleted() != AgentDeleteConstants.NOT_DELETED) {
            throw new BusinessException("交付不存在");
        }
        return delivery;
    }

    private AgentDelivery getDeletedDeliveryOrThrow(Long id) {
        AgentDelivery delivery = deliveryMapper.selectByIdIncludeDeleted(id);
        if (delivery == null || delivery.getDeleted() == null || delivery.getDeleted() != AgentDeleteConstants.DELETED) {
            throw new BusinessException("回收站中不存在该交付");
        }
        return delivery;
    }

    private AgentProject getActiveProjectOrThrow(Long projectId) {
        AgentProject project = projectMapper.selectById(projectId);
        if (project == null || project.getDeleted() == null || project.getDeleted() != AgentDeleteConstants.NOT_DELETED) {
            throw new BusinessException("所属项目不存在");
        }
        return project;
    }

    private AgentCustomer getActiveCustomerOrThrow(Long customerId, Long projectId) {
        AgentCustomer customer = customerMapper.selectById(customerId);
        if (customer == null || customer.getDeleted() == null || customer.getDeleted() != AgentDeleteConstants.NOT_DELETED) {
            throw new BusinessException("所属客户不存在");
        }
        if (!projectId.equals(customer.getProjectId())) {
            throw new BusinessException("客户与项目不匹配");
        }
        return customer;
    }

    private void validateSaveBo(AgentDeliverySaveBo bo, boolean isUpdate) {
        if (!isUpdate && bo.getId() != null) {
            throw new BusinessException("新增交付不能携带ID");
        }
        if (bo.getProjectId() == null) {
            throw new BusinessException("所属项目不能为空");
        }
        if (bo.getCustomerId() == null) {
            throw new BusinessException("所属客户不能为空");
        }
        if (StrUtil.isBlank(bo.getName())) {
            throw new BusinessException("交付名称不能为空");
        }
    }

    private Map<Long, String> loadProjectNameMap(List<AgentDelivery> deliveries) {
        return deliveries.stream()
                .map(AgentDelivery::getProjectId)
                .distinct()
                .collect(Collectors.toMap(projectId -> projectId, projectId -> {
                    AgentProject project = projectMapper.selectById(projectId);
                    return project == null ? null : project.getName();
                }, (a, b) -> a));
    }

    private Map<Long, String> loadCustomerNameMap(List<AgentDelivery> deliveries) {
        return deliveries.stream()
                .map(AgentDelivery::getCustomerId)
                .distinct()
                .collect(Collectors.toMap(customerId -> customerId, customerId -> {
                    AgentCustomer customer = customerMapper.selectById(customerId);
                    return customer == null ? null : customer.getName();
                }, (a, b) -> a));
    }

    private AgentDeliveryVo toVo(AgentDelivery delivery, String projectName, String customerName) {
        AgentDeliveryVo vo = new AgentDeliveryVo();
        BeanUtil.copyProperties(delivery, vo);
        vo.setProjectName(projectName);
        vo.setCustomerName(customerName);
        return vo;
    }

    private int resolvePage(Integer page) {
        return page == null || page < 1 ? 1 : page;
    }

    private int resolvePageSize(Integer pageSize) {
        return pageSize == null || pageSize < 1 ? 10 : pageSize;
    }
}

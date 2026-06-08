package com.sqx.modules.agent.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.common.PageResult;
import com.sqx.common.exception.BusinessException;
import com.sqx.modules.agent.db.entity.AgentProject;
import com.sqx.modules.agent.db.mapper.AgentCustomerMapper;
import com.sqx.modules.agent.db.mapper.AgentDeliveryMapper;
import com.sqx.modules.agent.db.mapper.AgentProjectMapper;
import com.sqx.modules.agent.domain.bo.AgentPageQueryBo;
import com.sqx.modules.agent.domain.bo.AgentProjectSaveBo;
import com.sqx.modules.agent.domain.vo.AgentProjectVo;
import com.sqx.modules.agent.service.AgentProjectService;
import com.sqx.modules.agent.support.AgentDeleteConstants;
import com.sqx.modules.agent.support.DirectoryChecker;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgentProjectServiceImpl implements AgentProjectService {

    private final AgentProjectMapper projectMapper;
    private final AgentCustomerMapper customerMapper;
    private final AgentDeliveryMapper deliveryMapper;

    public AgentProjectServiceImpl(AgentProjectMapper projectMapper,
                                   AgentCustomerMapper customerMapper,
                                   AgentDeliveryMapper deliveryMapper) {
        this.projectMapper = projectMapper;
        this.customerMapper = customerMapper;
        this.deliveryMapper = deliveryMapper;
    }

    @Override
    public PageResult<AgentProjectVo> page(AgentPageQueryBo queryBo) {
        int page = resolvePage(queryBo.getPage());
        int pageSize = resolvePageSize(queryBo.getPageSize());

        LambdaQueryWrapper<AgentProject> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgentProject::getDeleted, AgentDeleteConstants.NOT_DELETED);
        if (StrUtil.isNotBlank(queryBo.getName())) {
            wrapper.like(AgentProject::getName, queryBo.getName());
        }
        wrapper.orderByDesc(AgentProject::getCreateTime);

        Page<AgentProject> pageData = projectMapper.selectPage(new Page<>(page, pageSize), wrapper);
        List<AgentProjectVo> list = pageData.getRecords().stream().map(this::toVo).collect(Collectors.toList());
        return PageResult.of(list, pageData.getTotal(), page, pageSize);
    }

    @Override
    public AgentProjectVo detail(Long id) {
        return toVo(getActiveProjectOrThrow(id));
    }

    @Override
    public void save(AgentProjectSaveBo bo) {
        validateSaveBo(bo, false);
        Date now = new Date();
        AgentProject project = new AgentProject();
        BeanUtil.copyProperties(bo, project);
        project.setDeleted(AgentDeleteConstants.NOT_DELETED);
        project.setCreateTime(now);
        project.setUpdateTime(now);
        projectMapper.insert(project);
    }

    @Override
    public void update(AgentProjectSaveBo bo) {
        if (bo.getId() == null) {
            throw new BusinessException("项目ID不能为空");
        }
        validateSaveBo(bo, true);
        AgentProject project = getActiveProjectOrThrow(bo.getId());
        project.setName(bo.getName());
        project.setCode(bo.getCode());
        project.setDirectoryPath(bo.getDirectoryPath());
        project.setDescription(bo.getDescription());
        project.setUpdateTime(new Date());
        projectMapper.updateById(project);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void softDelete(Long id) {
        getActiveProjectOrThrow(id);
        Date now = new Date();
        int affected = projectMapper.softDeleteById(id, now);
        if (affected == 0) {
            throw new BusinessException("项目不存在或已删除");
        }
        customerMapper.softDeleteByProjectId(id, now);
        deliveryMapper.softDeleteByProjectId(id, now);
    }

    @Override
    public PageResult<AgentProjectVo> recyclePage(AgentPageQueryBo queryBo) {
        int page = resolvePage(queryBo.getPage());
        int pageSize = resolvePageSize(queryBo.getPageSize());
        Page<AgentProject> pageData = projectMapper.selectRecyclePage(new Page<>(page, pageSize));
        List<AgentProjectVo> list = pageData.getRecords().stream().map(this::toVo).collect(Collectors.toList());
        return PageResult.of(list, pageData.getTotal(), page, pageSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void restore(Long id) {
        AgentProject project = getDeletedProjectOrThrow(id);
        if (StrUtil.isNotBlank(project.getDirectoryPath()) && !DirectoryChecker.exists(project.getDirectoryPath())) {
            throw new BusinessException("项目目录不存在，无法恢复");
        }
        Date now = new Date();
        int affected = projectMapper.restoreById(id, now);
        if (affected == 0) {
            throw new BusinessException("项目恢复失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void permanentDelete(Long id) {
        getDeletedProjectOrThrow(id);
        int affected = projectMapper.permanentDeleteById(id);
        if (affected == 0) {
            throw new BusinessException("项目彻底删除失败");
        }
    }

    private AgentProject getActiveProjectOrThrow(Long id) {
        AgentProject project = projectMapper.selectById(id);
        if (project == null || project.getDeleted() == null || project.getDeleted() != AgentDeleteConstants.NOT_DELETED) {
            throw new BusinessException("项目不存在");
        }
        return project;
    }

    private AgentProject getDeletedProjectOrThrow(Long id) {
        AgentProject project = projectMapper.selectByIdIncludeDeleted(id);
        if (project == null || project.getDeleted() == null || project.getDeleted() != AgentDeleteConstants.DELETED) {
            throw new BusinessException("回收站中不存在该项目");
        }
        return project;
    }

    private void validateSaveBo(AgentProjectSaveBo bo, boolean isUpdate) {
        if (!isUpdate && bo.getId() != null) {
            throw new BusinessException("新增项目不能携带ID");
        }
        if (StrUtil.isBlank(bo.getName())) {
            throw new BusinessException("项目名称不能为空");
        }
        if (StrUtil.isBlank(bo.getCode())) {
            throw new BusinessException("项目编码不能为空");
        }
    }

    private AgentProjectVo toVo(AgentProject project) {
        AgentProjectVo vo = new AgentProjectVo();
        BeanUtil.copyProperties(project, vo);
        return vo;
    }

    private int resolvePage(Integer page) {
        return page == null || page < 1 ? 1 : page;
    }

    private int resolvePageSize(Integer pageSize) {
        return pageSize == null || pageSize < 1 ? 10 : pageSize;
    }
}

package com.sqx.modules.agent.category.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.common.PageResult;
import com.sqx.common.exception.BusinessException;
import com.sqx.modules.agent.base.AbstractAgentService;
import com.sqx.modules.agent.base.AgentPageQueryBo;
import com.sqx.modules.agent.base.RecycleBinSupport;
import com.sqx.modules.agent.category.db.entity.AgentCategory;
import com.sqx.modules.agent.category.db.mapper.AgentCategoryMapper;
import com.sqx.modules.agent.category.domain.bo.CategorySaveBo;
import com.sqx.modules.agent.category.service.AgentCategoryService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AgentCategoryServiceImpl extends AbstractAgentService<AgentCategory, AgentCategoryMapper>
        implements AgentCategoryService {

    public AgentCategoryServiceImpl(AgentCategoryMapper mapper, RecycleBinSupport recycleBinSupport) {
        super(mapper, recycleBinSupport);
    }

    @Override
    protected String tableName() {
        return "agent_category";
    }

    @Override
    protected Class<AgentCategory> entityClass() {
        return AgentCategory.class;
    }

    @Override
    protected String entityLabel() {
        return "分类";
    }

    @Override
    protected com.baomidou.mybatisplus.core.toolkit.support.SFunction<AgentCategory, Date> createTimeColumn() {
        return AgentCategory::getCreateTime;
    }

    @Override
    public PageResult<AgentCategory> page(AgentPageQueryBo queryBo) {
        LambdaQueryWrapper<AgentCategory> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(queryBo.getKeyword())) {
            wrapper.like(AgentCategory::getName, queryBo.getKeyword());
        }
        if (queryBo.getStatus() != null) {
            wrapper.eq(AgentCategory::getStatus, queryBo.getStatus());
        }
        return pageQuery(queryBo, wrapper);
    }

    @Override
    public List<AgentCategory> listAll() {
        LambdaQueryWrapper<AgentCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(AgentCategory::getSort).orderByAsc(AgentCategory::getId);
        return mapper.selectList(wrapper);
    }

    @Override
    public AgentCategory detail(Long id) {
        return getOrThrow(id);
    }

    @Override
    public void save(CategorySaveBo bo) {
        validateSaveBo(bo, false);
        AgentCategory entity = new AgentCategory();
        entity.setName(bo.getName());
        entity.setParentId(bo.getParentId() == null ? 0L : bo.getParentId());
        entity.setSort(bo.getSort() == null ? 0 : bo.getSort());
        entity.setStatus(bo.getStatus() == null ? 1 : bo.getStatus());
        fillCreateTime(entity);
        mapper.insert(entity);
    }

    @Override
    public void update(CategorySaveBo bo) {
        if (bo.getId() == null) {
            throw new BusinessException("分类ID不能为空");
        }
        validateSaveBo(bo, true);
        AgentCategory entity = getOrThrow(bo.getId());
        entity.setName(bo.getName());
        if (bo.getParentId() != null) {
            entity.setParentId(bo.getParentId());
        }
        if (bo.getSort() != null) {
            entity.setSort(bo.getSort());
        }
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

    private void validateSaveBo(CategorySaveBo bo, boolean isUpdate) {
        if (!isUpdate && bo.getId() != null) {
            throw new BusinessException("新增分类不能携带ID");
        }
        if (StrUtil.isBlank(bo.getName())) {
            throw new BusinessException("分类名称不能为空");
        }
    }
}

package com.sqx.modules.agent.base;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.common.PageResult;
import com.sqx.common.exception.BusinessException;

import java.util.Date;

/**
 * Agent 模块软删除 + 回收站通用服务基类
 */
public abstract class AbstractAgentService<T extends BaseAgentEntity, M extends BaseMapper<T>> {

    protected final M mapper;
    protected final RecycleBinSupport recycleBinSupport;

    protected AbstractAgentService(M mapper, RecycleBinSupport recycleBinSupport) {
        this.mapper = mapper;
        this.recycleBinSupport = recycleBinSupport;
    }

    protected abstract String tableName();

    protected abstract Class<T> entityClass();

    protected abstract String entityLabel();

    protected abstract SFunction<T, Date> createTimeColumn();

    protected T getOrThrow(Long id) {
        T entity = mapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(entityLabel() + "不存在");
        }
        return entity;
    }

    protected int resolvePage(Integer page) {
        return page == null || page < 1 ? 1 : page;
    }

    protected int resolvePageSize(Integer pageSize) {
        return pageSize == null || pageSize < 1 ? 10 : pageSize;
    }

    protected PageResult<T> pageQuery(AgentPageQueryBo queryBo, LambdaQueryWrapper<T> wrapper) {
        int page = resolvePage(queryBo.getPage());
        int pageSize = resolvePageSize(queryBo.getPageSize());
        wrapper.orderByDesc(createTimeColumn());
        Page<T> pageData = mapper.selectPage(new Page<>(page, pageSize), wrapper);
        return PageResult.of(pageData.getRecords(), pageData.getTotal(), page, pageSize);
    }

    public void softDelete(Long id) {
        T entity = getOrThrow(id);
        Date now = new Date();
        entity.setDeleteTime(now);
        entity.setUpdateTime(now);
        mapper.updateById(entity);
        mapper.deleteById(id);
    }

    public PageResult<T> recyclePage(AgentPageQueryBo queryBo) {
        int page = resolvePage(queryBo.getPage());
        int pageSize = resolvePageSize(queryBo.getPageSize());
        return recycleBinSupport.page(tableName(), entityClass(), page, pageSize);
    }

    public void restore(Long id) {
        recycleBinSupport.restore(tableName(), id);
    }

    public void permanentDelete(Long id) {
        recycleBinSupport.permanentDelete(tableName(), id);
    }

    protected void fillCreateTime(T entity) {
        Date now = new Date();
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        entity.setDeleted(0);
    }

    protected void touchUpdateTime(T entity) {
        entity.setUpdateTime(new Date());
    }
}

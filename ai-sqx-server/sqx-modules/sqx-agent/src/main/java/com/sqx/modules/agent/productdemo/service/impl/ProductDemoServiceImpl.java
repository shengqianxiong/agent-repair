package com.sqx.modules.agent.productdemo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.common.PageResult;
import com.sqx.common.exception.BusinessException;
import com.sqx.modules.agent.base.AbstractAgentService;
import com.sqx.modules.agent.base.AgentPageQueryBo;
import com.sqx.modules.agent.base.RecycleBinSupport;
import com.sqx.modules.agent.productdemo.db.entity.ProductDemo;
import com.sqx.modules.agent.productdemo.db.mapper.ProductDemoMapper;
import com.sqx.modules.agent.productdemo.domain.bo.ProductDemoSaveBo;
import com.sqx.modules.agent.productdemo.service.ProductDemoService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ProductDemoServiceImpl extends AbstractAgentService<ProductDemo, ProductDemoMapper>
        implements ProductDemoService {

    public ProductDemoServiceImpl(ProductDemoMapper mapper, RecycleBinSupport recycleBinSupport) {
        super(mapper, recycleBinSupport);
    }

    @Override
    protected String tableName() {
        return "product_demo";
    }

    @Override
    protected Class<ProductDemo> entityClass() {
        return ProductDemo.class;
    }

    @Override
    protected String entityLabel() {
        return "产品演示";
    }

    @Override
    protected com.baomidou.mybatisplus.core.toolkit.support.SFunction<ProductDemo, Date> createTimeColumn() {
        return ProductDemo::getCreateTime;
    }

    @Override
    public PageResult<ProductDemo> page(AgentPageQueryBo queryBo) {
        LambdaQueryWrapper<ProductDemo> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(queryBo.getKeyword())) {
            wrapper.like(ProductDemo::getName, queryBo.getKeyword());
        }
        if (queryBo.getStatus() != null) {
            wrapper.eq(ProductDemo::getStatus, queryBo.getStatus());
        }
        return pageQuery(queryBo, wrapper);
    }

    @Override
    public ProductDemo detail(Long id) {
        return getOrThrow(id);
    }

    @Override
    public void save(ProductDemoSaveBo bo) {
        validateSaveBo(bo, false);
        ProductDemo entity = new ProductDemo();
        BeanUtil.copyProperties(bo, entity);
        entity.setStatus(bo.getStatus() == null ? 1 : bo.getStatus());
        fillCreateTime(entity);
        mapper.insert(entity);
    }

    @Override
    public void update(ProductDemoSaveBo bo) {
        if (bo.getId() == null) {
            throw new BusinessException("产品演示ID不能为空");
        }
        validateSaveBo(bo, true);
        ProductDemo entity = getOrThrow(bo.getId());
        entity.setName(bo.getName());
        entity.setProductId(bo.getProductId());
        entity.setDemoUrl(bo.getDemoUrl());
        entity.setContent(bo.getContent());
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

    private void validateSaveBo(ProductDemoSaveBo bo, boolean isUpdate) {
        if (!isUpdate && bo.getId() != null) {
            throw new BusinessException("新增产品演示不能携带ID");
        }
        if (StrUtil.isBlank(bo.getName())) {
            throw new BusinessException("产品演示名称不能为空");
        }
    }
}

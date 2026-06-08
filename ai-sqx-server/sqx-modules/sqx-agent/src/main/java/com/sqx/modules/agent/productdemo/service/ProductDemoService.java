package com.sqx.modules.agent.productdemo.service;

import com.sqx.common.PageResult;
import com.sqx.modules.agent.base.AgentPageQueryBo;
import com.sqx.modules.agent.productdemo.db.entity.ProductDemo;
import com.sqx.modules.agent.productdemo.domain.bo.ProductDemoSaveBo;

public interface ProductDemoService {

    PageResult<ProductDemo> page(AgentPageQueryBo queryBo);

    ProductDemo detail(Long id);

    void save(ProductDemoSaveBo bo);

    void update(ProductDemoSaveBo bo);

    void delete(Long id);

    PageResult<ProductDemo> recyclePage(AgentPageQueryBo queryBo);

    void restore(Long id);

    void permanentDelete(Long id);
}

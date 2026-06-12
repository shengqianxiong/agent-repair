package com.sqx.modules.bar.service;

import com.sqx.common.PageResult;
import com.sqx.modules.bar.domain.bo.ProductQueryBo;
import com.sqx.modules.bar.domain.bo.ProductSaveBo;
import com.sqx.modules.bar.domain.vo.ProductVo;

public interface BarProductService {

    PageResult<ProductVo> page(ProductQueryBo queryBo);

    ProductVo detail(Long id);

    void save(ProductSaveBo bo);

    void update(ProductSaveBo bo);

    void delete(Long id);
}

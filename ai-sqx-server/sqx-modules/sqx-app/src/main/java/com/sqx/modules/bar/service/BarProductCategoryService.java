package com.sqx.modules.bar.service;

import com.sqx.modules.bar.domain.bo.CategorySaveBo;
import com.sqx.modules.bar.domain.vo.ProductCategoryVo;

import java.util.List;

public interface BarProductCategoryService {

    List<ProductCategoryVo> listEnabled();

    List<ProductCategoryVo> listAll();

    void save(CategorySaveBo bo);
}

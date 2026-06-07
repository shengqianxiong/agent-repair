package com.sqx.modules.bar.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.common.PageResult;
import com.sqx.common.exception.BusinessException;
import com.sqx.modules.bar.db.entity.BarProduct;
import com.sqx.modules.bar.db.entity.BarProductCategory;
import com.sqx.modules.bar.db.mapper.BarProductCategoryMapper;
import com.sqx.modules.bar.db.mapper.BarProductMapper;
import com.sqx.modules.bar.domain.bo.ProductQueryBo;
import com.sqx.modules.bar.domain.bo.ProductSaveBo;
import com.sqx.modules.bar.domain.vo.ProductVo;
import com.sqx.modules.bar.service.BarProductService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BarProductServiceImpl implements BarProductService {

    private final BarProductMapper productMapper;
    private final BarProductCategoryMapper categoryMapper;

    public BarProductServiceImpl(BarProductMapper productMapper,
                                 BarProductCategoryMapper categoryMapper) {
        this.productMapper = productMapper;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public PageResult<ProductVo> page(ProductQueryBo queryBo) {
        int page = queryBo.resolvePage();
        int pageSize = queryBo.getPageSize() == null || queryBo.getPageSize() < 1 ? 10 : queryBo.getPageSize();

        LambdaQueryWrapper<BarProduct> wrapper = buildQueryWrapper(queryBo);
        wrapper.orderByDesc(BarProduct::getCreateTime);

        Page<BarProduct> pageData = productMapper.selectPage(new Page<>(page, pageSize), wrapper);
        List<ProductVo> list = toVoList(pageData.getRecords());
        return PageResult.of(list, pageData.getTotal(), page, pageSize);
    }

    @Override
    public ProductVo detail(Long id) {
        BarProduct product = getProductOrThrow(id);
        return toVo(product);
    }

    @Override
    public void save(ProductSaveBo bo) {
        validateSaveBo(bo, false);
        Date now = new Date();
        BarProduct product = new BarProduct();
        BeanUtil.copyProperties(bo, product);
        product.setStatus(bo.getStatus() == null ? 1 : bo.getStatus());
        product.setStock(bo.getStock() == null ? 0 : bo.getStock());
        product.setCreateTime(now);
        product.setUpdateTime(now);
        productMapper.insert(product);
    }

    @Override
    public void update(ProductSaveBo bo) {
        if (bo.getId() == null) {
            throw new BusinessException("商品ID不能为空");
        }
        validateSaveBo(bo, true);
        BarProduct product = getProductOrThrow(bo.getId());
        product.setName(bo.getName());
        product.setCategoryId(bo.getCategoryId());
        product.setImage(bo.getImage());
        product.setPrice(bo.getPrice());
        product.setOriginalPrice(bo.getOriginalPrice());
        product.setDescription(bo.getDescription());
        if (bo.getStock() != null) {
            product.setStock(bo.getStock());
        }
        if (bo.getStatus() != null) {
            product.setStatus(bo.getStatus());
        }
        product.setUpdateTime(new Date());
        productMapper.updateById(product);
    }

    @Override
    public void delete(Long id) {
        getProductOrThrow(id);
        productMapper.deleteById(id);
    }

    private LambdaQueryWrapper<BarProduct> buildQueryWrapper(ProductQueryBo queryBo) {
        LambdaQueryWrapper<BarProduct> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(queryBo.getName())) {
            wrapper.like(BarProduct::getName, queryBo.getName());
        }
        if (queryBo.getCategoryId() != null) {
            wrapper.eq(BarProduct::getCategoryId, queryBo.getCategoryId());
        }
        if (queryBo.getStatus() != null) {
            wrapper.eq(BarProduct::getStatus, queryBo.getStatus());
        }
        return wrapper;
    }

    private void validateSaveBo(ProductSaveBo bo, boolean isUpdate) {
        if (!isUpdate && bo.getId() != null) {
            throw new BusinessException("新增商品不能携带ID");
        }
        if (StrUtil.isBlank(bo.getName())) {
            throw new BusinessException("商品名称不能为空");
        }
        if (bo.getCategoryId() == null) {
            throw new BusinessException("商品分类不能为空");
        }
        if (bo.getPrice() == null) {
            throw new BusinessException("商品售价不能为空");
        }
        BarProductCategory category = categoryMapper.selectById(bo.getCategoryId());
        if (category == null) {
            throw new BusinessException("商品分类不存在");
        }
    }

    private BarProduct getProductOrThrow(Long id) {
        BarProduct product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        return product;
    }

    private List<ProductVo> toVoList(List<BarProduct> products) {
        Map<Long, String> categoryNameMap = categoryMapper.selectList(null).stream()
                .collect(Collectors.toMap(BarProductCategory::getId, BarProductCategory::getName, (a, b) -> a));
        return products.stream()
                .map(product -> toVo(product, categoryNameMap.get(product.getCategoryId())))
                .collect(Collectors.toList());
    }

    private ProductVo toVo(BarProduct product) {
        BarProductCategory category = categoryMapper.selectById(product.getCategoryId());
        String categoryName = category == null ? null : category.getName();
        return toVo(product, categoryName);
    }

    private ProductVo toVo(BarProduct product, String categoryName) {
        ProductVo vo = new ProductVo();
        BeanUtil.copyProperties(product, vo);
        vo.setCategoryName(categoryName);
        return vo;
    }
}

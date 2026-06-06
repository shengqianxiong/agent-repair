package com.sqx.modules.bar.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.common.exception.BusinessException;
import com.sqx.modules.bar.db.entity.BarProductCategory;
import com.sqx.modules.bar.db.mapper.BarProductCategoryMapper;
import com.sqx.modules.bar.domain.bo.CategorySaveBo;
import com.sqx.modules.bar.domain.vo.ProductCategoryVo;
import com.sqx.modules.bar.service.BarProductCategoryService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BarProductCategoryServiceImpl implements BarProductCategoryService {

    private final BarProductCategoryMapper categoryMapper;

    public BarProductCategoryServiceImpl(BarProductCategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<ProductCategoryVo> listEnabled() {
        LambdaQueryWrapper<BarProductCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BarProductCategory::getStatus, 1)
                .orderByAsc(BarProductCategory::getSort)
                .orderByAsc(BarProductCategory::getId);
        return toVoList(categoryMapper.selectList(wrapper));
    }

    @Override
    public List<ProductCategoryVo> listAll() {
        LambdaQueryWrapper<BarProductCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(BarProductCategory::getSort)
                .orderByAsc(BarProductCategory::getId);
        return toVoList(categoryMapper.selectList(wrapper));
    }

    @Override
    public void save(CategorySaveBo bo) {
        if (StrUtil.isBlank(bo.getName())) {
            throw new BusinessException("分类名称不能为空");
        }
        Date now = new Date();
        if (bo.getId() == null) {
            BarProductCategory category = new BarProductCategory();
            category.setName(bo.getName());
            category.setSort(bo.getSort() == null ? 0 : bo.getSort());
            category.setStatus(bo.getStatus() == null ? 1 : bo.getStatus());
            category.setCreateTime(now);
            category.setUpdateTime(now);
            categoryMapper.insert(category);
            return;
        }
        BarProductCategory category = categoryMapper.selectById(bo.getId());
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        category.setName(bo.getName());
        if (bo.getSort() != null) {
            category.setSort(bo.getSort());
        }
        if (bo.getStatus() != null) {
            category.setStatus(bo.getStatus());
        }
        category.setUpdateTime(now);
        categoryMapper.updateById(category);
    }

    private List<ProductCategoryVo> toVoList(List<BarProductCategory> categories) {
        return categories.stream().map(category -> {
            ProductCategoryVo vo = new ProductCategoryVo();
            BeanUtil.copyProperties(category, vo);
            return vo;
        }).collect(Collectors.toList());
    }
}

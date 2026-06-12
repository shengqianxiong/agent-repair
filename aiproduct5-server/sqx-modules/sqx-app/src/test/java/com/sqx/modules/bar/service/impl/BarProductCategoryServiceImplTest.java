package com.sqx.modules.bar.service.impl;

import com.sqx.common.exception.BusinessException;
import com.sqx.modules.bar.db.entity.BarProductCategory;
import com.sqx.modules.bar.db.mapper.BarProductCategoryMapper;
import com.sqx.modules.bar.domain.bo.CategorySaveBo;
import com.sqx.modules.bar.domain.vo.ProductCategoryVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BarProductCategoryServiceImplTest {

    @Mock
    private BarProductCategoryMapper categoryMapper;

    @InjectMocks
    private BarProductCategoryServiceImpl categoryService;

    private BarProductCategory category;

    @BeforeEach
    void setUp() {
        category = new BarProductCategory();
        category.setId(1L);
        category.setName("啤酒");
        category.setSort(1);
        category.setStatus(1);
    }

    @Test
    void listEnabled_shouldReturnEnabledCategories() {
        when(categoryMapper.selectList(any())).thenReturn(Collections.singletonList(category));

        List<ProductCategoryVo> list = categoryService.listEnabled();

        assertEquals(1, list.size());
        assertEquals("啤酒", list.get(0).getName());
    }

    @Test
    void listAll_shouldReturnAllCategories() {
        when(categoryMapper.selectList(any())).thenReturn(Collections.singletonList(category));

        List<ProductCategoryVo> list = categoryService.listAll();

        assertEquals(1, list.size());
        assertEquals("啤酒", list.get(0).getName());
    }

    @Test
    void save_shouldInsertNewCategory() {
        CategorySaveBo bo = new CategorySaveBo();
        bo.setName("洋酒");
        bo.setSort(2);

        categoryService.save(bo);

        ArgumentCaptor<BarProductCategory> captor = ArgumentCaptor.forClass(BarProductCategory.class);
        verify(categoryMapper).insert(captor.capture());
        assertEquals("洋酒", captor.getValue().getName());
        assertEquals(2, captor.getValue().getSort());
        assertEquals(1, captor.getValue().getStatus());
    }

    @Test
    void save_shouldUpdateExistingCategory() {
        CategorySaveBo bo = new CategorySaveBo();
        bo.setId(1L);
        bo.setName("精酿啤酒");
        bo.setSort(3);
        bo.setStatus(0);

        when(categoryMapper.selectById(1L)).thenReturn(category);

        categoryService.save(bo);

        verify(categoryMapper).updateById(any(BarProductCategory.class));
    }

    @Test
    void save_shouldThrowWhenNameBlank() {
        CategorySaveBo bo = new CategorySaveBo();

        BusinessException ex = assertThrows(BusinessException.class, () -> categoryService.save(bo));
        assertEquals("分类名称不能为空", ex.getMessage());
    }

    @Test
    void save_shouldThrowWhenUpdateTargetNotFound() {
        CategorySaveBo bo = new CategorySaveBo();
        bo.setId(99L);
        bo.setName("不存在");

        when(categoryMapper.selectById(99L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> categoryService.save(bo));
        assertEquals("分类不存在", ex.getMessage());
    }
}

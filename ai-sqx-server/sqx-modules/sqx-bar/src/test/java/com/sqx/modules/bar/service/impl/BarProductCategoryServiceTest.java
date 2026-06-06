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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BarProductCategoryServiceTest {

    @Mock
    private BarProductCategoryMapper categoryMapper;

    @InjectMocks
    private BarProductCategoryServiceImpl categoryService;

    private BarProductCategory enabledCategory;
    private BarProductCategory disabledCategory;

    @BeforeEach
    void setUp() {
        enabledCategory = new BarProductCategory();
        enabledCategory.setId(1L);
        enabledCategory.setName("啤酒");
        enabledCategory.setSort(1);
        enabledCategory.setStatus(1);

        disabledCategory = new BarProductCategory();
        disabledCategory.setId(2L);
        disabledCategory.setName("已下架分类");
        disabledCategory.setSort(2);
        disabledCategory.setStatus(0);
    }

    @Test
    void listEnabled_shouldReturnOnlyEnabledCategories() {
        when(categoryMapper.selectList(any())).thenReturn(Arrays.asList(enabledCategory));

        List<ProductCategoryVo> list = categoryService.listEnabled();

        assertEquals(1, list.size());
        assertEquals("啤酒", list.get(0).getName());
    }

    @Test
    void listAll_shouldReturnAllCategories() {
        when(categoryMapper.selectList(any())).thenReturn(Arrays.asList(enabledCategory, disabledCategory));

        List<ProductCategoryVo> list = categoryService.listAll();

        assertEquals(2, list.size());
    }

    @Test
    void save_shouldInsertNewCategory() {
        CategorySaveBo bo = new CategorySaveBo();
        bo.setName("鸡尾酒");
        bo.setSort(2);

        categoryService.save(bo);

        ArgumentCaptor<BarProductCategory> captor = ArgumentCaptor.forClass(BarProductCategory.class);
        verify(categoryMapper).insert(captor.capture());
        BarProductCategory saved = captor.getValue();
        assertEquals("鸡尾酒", saved.getName());
        assertEquals(2, saved.getSort());
        assertEquals(1, saved.getStatus());
    }

    @Test
    void save_shouldUpdateExistingCategory() {
        CategorySaveBo bo = new CategorySaveBo();
        bo.setId(1L);
        bo.setName("精酿啤酒");
        bo.setSort(1);
        bo.setStatus(1);

        when(categoryMapper.selectById(1L)).thenReturn(enabledCategory);

        categoryService.save(bo);

        verify(categoryMapper).updateById(any(BarProductCategory.class));
    }

    @Test
    void save_shouldRejectBlankName() {
        CategorySaveBo bo = new CategorySaveBo();

        BusinessException ex = assertThrows(BusinessException.class, () -> categoryService.save(bo));
        assertEquals("分类名称不能为空", ex.getMessage());
    }

    @Test
    void save_shouldRejectUnknownCategoryOnUpdate() {
        CategorySaveBo bo = new CategorySaveBo();
        bo.setId(99L);
        bo.setName("不存在");

        when(categoryMapper.selectById(99L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> categoryService.save(bo));
        assertEquals("分类不存在", ex.getMessage());
    }
}

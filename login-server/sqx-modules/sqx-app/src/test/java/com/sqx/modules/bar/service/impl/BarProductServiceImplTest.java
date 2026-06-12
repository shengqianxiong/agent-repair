package com.sqx.modules.bar.service.impl;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BarProductServiceImplTest {

    @Mock
    private BarProductMapper productMapper;

    @Mock
    private BarProductCategoryMapper categoryMapper;

    @InjectMocks
    private BarProductServiceImpl productService;

    private BarProductCategory category;
    private BarProduct product;

    @BeforeEach
    void setUp() {
        category = new BarProductCategory();
        category.setId(1L);
        category.setName("啤酒");

        product = new BarProduct();
        product.setId(10L);
        product.setName("青岛啤酒");
        product.setCategoryId(1L);
        product.setPrice(new BigDecimal("18.00"));
        product.setStatus(1);
        product.setStock(100);
    }

    @Test
    void page_shouldReturnPagedProducts() {
        ProductQueryBo queryBo = new ProductQueryBo();
        queryBo.setPage(1);
        queryBo.setPageSize(10);

        Page<BarProduct> pageData = new Page<>(1, 10);
        pageData.setRecords(Collections.singletonList(product));
        pageData.setTotal(1);

        when(productMapper.selectPage(any(Page.class), any())).thenReturn(pageData);
        when(categoryMapper.selectList(any())).thenReturn(Collections.singletonList(category));

        PageResult<ProductVo> result = productService.page(queryBo);

        assertEquals(1, result.getTotal());
        assertEquals(1, result.getList().size());
        assertEquals("青岛啤酒", result.getList().get(0).getName());
        assertEquals("啤酒", result.getList().get(0).getCategoryName());
    }

    @Test
    void detail_shouldReturnProductVo() {
        when(productMapper.selectById(10L)).thenReturn(product);
        when(categoryMapper.selectById(1L)).thenReturn(category);

        ProductVo vo = productService.detail(10L);

        assertNotNull(vo);
        assertEquals("青岛啤酒", vo.getName());
        assertEquals("啤酒", vo.getCategoryName());
    }

    @Test
    void detail_shouldThrowWhenProductNotFound() {
        when(productMapper.selectById(99L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> productService.detail(99L));
        assertEquals("商品不存在", ex.getMessage());
    }

    @Test
    void save_shouldInsertProduct() {
        ProductSaveBo bo = new ProductSaveBo();
        bo.setName("百威啤酒");
        bo.setCategoryId(1L);
        bo.setPrice(new BigDecimal("22.00"));

        when(categoryMapper.selectById(1L)).thenReturn(category);

        productService.save(bo);

        ArgumentCaptor<BarProduct> captor = ArgumentCaptor.forClass(BarProduct.class);
        verify(productMapper).insert(captor.capture());
        assertEquals("百威啤酒", captor.getValue().getName());
        assertEquals(1, captor.getValue().getStatus());
        assertEquals(0, captor.getValue().getStock());
    }

    @Test
    void save_shouldThrowWhenNameBlank() {
        ProductSaveBo bo = new ProductSaveBo();
        bo.setCategoryId(1L);
        bo.setPrice(new BigDecimal("22.00"));

        BusinessException ex = assertThrows(BusinessException.class, () -> productService.save(bo));
        assertEquals("商品名称不能为空", ex.getMessage());
    }

    @Test
    void save_shouldThrowWhenCategoryNotFound() {
        ProductSaveBo bo = new ProductSaveBo();
        bo.setName("百威啤酒");
        bo.setCategoryId(99L);
        bo.setPrice(new BigDecimal("22.00"));

        when(categoryMapper.selectById(99L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> productService.save(bo));
        assertEquals("商品分类不存在", ex.getMessage());
    }

    @Test
    void update_shouldUpdateProduct() {
        ProductSaveBo bo = new ProductSaveBo();
        bo.setId(10L);
        bo.setName("青岛纯生");
        bo.setCategoryId(1L);
        bo.setPrice(new BigDecimal("25.00"));
        bo.setStatus(0);

        when(productMapper.selectById(10L)).thenReturn(product);
        when(categoryMapper.selectById(1L)).thenReturn(category);

        productService.update(bo);

        verify(productMapper).updateById(any(BarProduct.class));
    }

    @Test
    void update_shouldThrowWhenIdMissing() {
        ProductSaveBo bo = new ProductSaveBo();
        bo.setName("青岛纯生");
        bo.setCategoryId(1L);
        bo.setPrice(new BigDecimal("25.00"));

        BusinessException ex = assertThrows(BusinessException.class, () -> productService.update(bo));
        assertEquals("商品ID不能为空", ex.getMessage());
    }

    @Test
    void delete_shouldRemoveProduct() {
        when(productMapper.selectById(10L)).thenReturn(product);

        productService.delete(10L);

        verify(productMapper).deleteById(10L);
    }

    @Test
    void delete_shouldThrowWhenProductNotFound() {
        when(productMapper.selectById(99L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> productService.delete(99L));
        assertEquals("商品不存在", ex.getMessage());
    }
}

package com.sqx.modules.review.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.common.exception.BusinessException;
import com.sqx.modules.review.db.entity.ReviewActivity;
import com.sqx.modules.review.db.mapper.ReviewActivityMapper;
import com.sqx.modules.review.domain.bo.ActivityQueryBo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewActivityServiceImplTest {

    @Mock
    private ReviewActivityMapper activityMapper;
    @Mock
    private com.sqx.modules.review.db.mapper.ReviewTaskMapper taskMapper;
    @Mock
    private com.sqx.modules.review.db.mapper.ReviewRebateMapper rebateMapper;

    @InjectMocks
    private ReviewActivityServiceImpl activityService;

    private ReviewActivity activeActivity;

    @BeforeEach
    void setUp() {
        activeActivity = new ReviewActivity();
        activeActivity.setId(10001L);
        activeActivity.setCode("DP20260611");
        activeActivity.setTitle("满杯鲜萃茶饮评价任务");
        activeActivity.setProductName("招牌厚乳拿铁");
        activeActivity.setStatus(1);
        activeActivity.setRebateAmount(new BigDecimal("20"));
        activeActivity.setValidEndTime(new Date(System.currentTimeMillis() + 86400000L));
        activeActivity.setCopywritingJson("[{\"id\":1,\"title\":\"真实体验版\",\"content\":\"很好喝\"}]");
        activeActivity.setRecommendImagesJson("[\"https://example.com/a.jpg\"]");
    }

    @Test
    @DisplayName("按 code 查询活动 - 正常返回")
    void getByCode_shouldReturnActivity() {
        when(activityMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(activeActivity);
        assertEquals("DP20260611", activityService.getByCode("DP20260611").getCode());
    }

    @Test
    @DisplayName("按 code 查询活动 - 不存在抛异常")
    void getByCode_notFound_shouldThrow() {
        when(activityMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        assertThrows(BusinessException.class, () -> activityService.getByCode("INVALID"));
    }

    @Test
    @DisplayName("活动列表筛选 - 关键词过滤")
    void page_withKeyword_shouldFilter() {
        ActivityQueryBo queryBo = new ActivityQueryBo();
        queryBo.setKeyword("拿铁");
        queryBo.setStatus(1);
        queryBo.setPage(1);
        queryBo.setPageSize(10);

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ReviewActivity> page =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 10);
        page.setRecords(Collections.singletonList(activeActivity));
        page.setTotal(1);
        when(activityMapper.selectPage(any(), any(LambdaQueryWrapper.class))).thenReturn(page);

        assertEquals(1, activityService.page(queryBo).getTotal());
        assertEquals("招牌厚乳拿铁", activityService.page(queryBo).getList().get(0).getProductName());
    }
}

package com.sqx.modules.review.service;

import com.sqx.common.PageResult;
import com.sqx.modules.review.domain.bo.ActivityQueryBo;
import com.sqx.modules.review.domain.bo.ActivitySaveBo;
import com.sqx.modules.review.domain.vo.ActivityVo;
import com.sqx.modules.review.domain.vo.AiHistoryVo;
import com.sqx.modules.review.domain.vo.DashboardVo;

public interface ReviewActivityService {

    ActivityVo getByCode(String code);

    ActivityVo create(ActivitySaveBo bo);

    ActivityVo update(ActivitySaveBo bo);

    PageResult<ActivityVo> page(ActivityQueryBo queryBo);

    ActivityVo detail(Long id);

    void updateStatus(Long id, Integer status);

    DashboardVo dashboard();

    PageResult<AiHistoryVo> aiHistory(Long activityId, Integer page, Integer pageSize);
}

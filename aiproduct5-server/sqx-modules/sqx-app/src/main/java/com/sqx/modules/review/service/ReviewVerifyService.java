package com.sqx.modules.review.service;

import com.sqx.common.PageResult;
import com.sqx.modules.review.domain.bo.VerifyActionBo;
import com.sqx.modules.review.domain.bo.VerifyQueryBo;
import com.sqx.modules.review.domain.vo.RebateVo;
import com.sqx.modules.review.domain.vo.TaskVo;

public interface ReviewVerifyService {

    PageResult<TaskVo> page(VerifyQueryBo queryBo);

    void pass(VerifyActionBo bo);

    void reject(VerifyActionBo bo);

    RebateVo rebateDetail(Long rebateId);
}

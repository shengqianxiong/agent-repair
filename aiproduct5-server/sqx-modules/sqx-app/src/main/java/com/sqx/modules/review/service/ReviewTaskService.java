package com.sqx.modules.review.service;

import com.sqx.common.PageResult;
import com.sqx.modules.review.domain.bo.TaskCopyReportBo;
import com.sqx.modules.review.domain.bo.TaskStartBo;
import com.sqx.modules.review.domain.bo.TaskSubmitBo;
import com.sqx.modules.review.domain.vo.TaskVo;
import com.sqx.modules.review.domain.vo.UserSummaryVo;

public interface ReviewTaskService {

    TaskVo start(TaskStartBo bo);

    void reportCopy(TaskCopyReportBo bo);

    TaskVo submit(TaskSubmitBo bo);

    TaskVo detail(Long taskId);

    UserSummaryVo userSummary(String userIdentifier);

    PageResult<TaskVo> userTasks(String userIdentifier, Integer page, Integer pageSize, String status);
}

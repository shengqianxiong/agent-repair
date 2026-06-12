package com.sqx.modules.review.controller.admin;

import com.sqx.common.PageResult;
import com.sqx.common.Result;
import com.sqx.modules.review.domain.vo.AiHistoryVo;
import com.sqx.modules.review.service.ReviewActivityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/ai")
public class AdminAiController {

    private final ReviewActivityService activityService;

    public AdminAiController(ReviewActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/history")
    public Result<PageResult<AiHistoryVo>> history(@RequestParam(required = false) Long activityId,
                                                   @RequestParam(required = false) Integer page,
                                                   @RequestParam(required = false) Integer pageSize) {
        return Result.ok(activityService.aiHistory(activityId, page, pageSize));
    }
}

package com.sqx.modules.review.controller.app;

import com.sqx.common.PageResult;
import com.sqx.common.Result;
import com.sqx.modules.review.domain.vo.TaskVo;
import com.sqx.modules.review.domain.vo.UserSummaryVo;
import com.sqx.modules.review.service.ReviewTaskService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/user")
public class AppUserReviewController {

    private final ReviewTaskService taskService;

    public AppUserReviewController(ReviewTaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/summary")
    public Result<UserSummaryVo> summary(@RequestParam String userIdentifier) {
        return Result.ok(taskService.userSummary(userIdentifier));
    }

    @GetMapping("/tasks")
    public Result<PageResult<TaskVo>> tasks(@RequestParam String userIdentifier,
                                            @RequestParam(required = false) Integer page,
                                            @RequestParam(required = false) Integer pageSize,
                                            @RequestParam(required = false) String status) {
        return Result.ok(taskService.userTasks(userIdentifier, page, pageSize, status));
    }
}

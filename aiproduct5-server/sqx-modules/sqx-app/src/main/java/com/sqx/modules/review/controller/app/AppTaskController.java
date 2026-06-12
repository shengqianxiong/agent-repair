package com.sqx.modules.review.controller.app;

import com.sqx.common.Result;
import com.sqx.modules.review.domain.bo.TaskCopyReportBo;
import com.sqx.modules.review.domain.bo.TaskStartBo;
import com.sqx.modules.review.domain.bo.TaskSubmitBo;
import com.sqx.modules.review.domain.vo.TaskVo;
import com.sqx.modules.review.service.ReviewTaskService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/task")
public class AppTaskController {

    private final ReviewTaskService taskService;

    public AppTaskController(ReviewTaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/start")
    public Result<TaskVo> start(@RequestBody TaskStartBo bo) {
        return Result.ok(taskService.start(bo));
    }

    @PostMapping("/copy-report")
    public Result<Void> copyReport(@RequestBody TaskCopyReportBo bo) {
        taskService.reportCopy(bo);
        return Result.ok();
    }

    @PostMapping("/submit")
    public Result<TaskVo> submit(@RequestBody TaskSubmitBo bo) {
        return Result.ok(taskService.submit(bo));
    }

    @GetMapping("/detail")
    public Result<TaskVo> detail(@RequestParam Long taskId) {
        return Result.ok(taskService.detail(taskId));
    }
}

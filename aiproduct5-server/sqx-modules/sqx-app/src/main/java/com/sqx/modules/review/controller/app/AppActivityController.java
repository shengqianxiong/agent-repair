package com.sqx.modules.review.controller.app;

import com.sqx.common.Result;
import com.sqx.modules.review.domain.vo.ActivityVo;
import com.sqx.modules.review.service.ReviewActivityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/activity")
public class AppActivityController {

    private final ReviewActivityService activityService;

    public AppActivityController(ReviewActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/by-code")
    public Result<ActivityVo> byCode(@RequestParam String code) {
        return Result.ok(activityService.getByCode(code));
    }
}

package com.sqx.modules.review.controller.admin;

import com.sqx.common.Result;
import com.sqx.modules.review.domain.vo.DashboardVo;
import com.sqx.modules.review.service.ReviewActivityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/seller")
public class AdminSellerController {

    private final ReviewActivityService activityService;

    public AdminSellerController(ReviewActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/dashboard")
    public Result<DashboardVo> dashboard() {
        return Result.ok(activityService.dashboard());
    }
}

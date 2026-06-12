package com.sqx.modules.review.controller.admin;

import com.sqx.common.PageResult;
import com.sqx.common.Result;
import com.sqx.modules.review.domain.bo.ActivityQueryBo;
import com.sqx.modules.review.domain.bo.ActivitySaveBo;
import com.sqx.modules.review.domain.vo.ActivityVo;
import com.sqx.modules.review.service.ReviewActivityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/activity")
public class AdminReviewActivityController {

    private final ReviewActivityService activityService;

    public AdminReviewActivityController(ReviewActivityService activityService) {
        this.activityService = activityService;
    }

    @PostMapping("/create")
    public Result<Map<String, Object>> create(@RequestBody ActivitySaveBo bo) {
        ActivityVo vo = activityService.create(bo);
        Map<String, Object> data = new HashMap<>();
        data.put("activityId", vo.getId());
        data.put("activity_id", vo.getId());
        data.put("qrcodeCode", vo.getCode());
        data.put("qrcodeUrl", vo.getQrcodeUrl());
        return Result.ok(data);
    }

    @PutMapping("/update")
    public Result<ActivityVo> update(@RequestBody ActivitySaveBo bo) {
        return Result.ok(activityService.update(bo));
    }

    @GetMapping("/list")
    public Result<PageResult<ActivityVo>> list(ActivityQueryBo queryBo) {
        return Result.ok(activityService.page(queryBo));
    }

    @GetMapping("/detail")
    public Result<ActivityVo> detail(@RequestParam Long id) {
        return Result.ok(activityService.detail(id));
    }

    @PostMapping("/status")
    public Result<Void> status(@RequestBody Map<String, Object> body) {
        Object idObj = body.get("id") != null ? body.get("id") : body.get("activityId");
        Object statusObj = body.get("status");
        if (idObj == null || statusObj == null) {
            return Result.error("活动ID和状态不能为空");
        }
        activityService.updateStatus(Long.valueOf(idObj.toString()), Integer.valueOf(statusObj.toString()));
        return Result.ok();
    }
}

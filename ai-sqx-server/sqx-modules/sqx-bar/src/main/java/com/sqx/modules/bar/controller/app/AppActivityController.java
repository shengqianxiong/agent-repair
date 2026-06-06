package com.sqx.modules.bar.controller.app;

import com.sqx.common.Result;
import com.sqx.common.annotation.Login;
import com.sqx.modules.bar.domain.vo.ActivityVo;
import com.sqx.modules.bar.service.BarActivityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户端 - 精选活动
 */
@RestController
@RequestMapping("/app/activity")
public class AppActivityController {

    private final BarActivityService activityService;

    public AppActivityController(BarActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/list")
    @Login
    public Result<List<ActivityVo>> list() {
        return Result.ok(activityService.listEnabled(20));
    }

    @GetMapping("/detail/{id}")
    @Login
    public Result<ActivityVo> detail(@PathVariable Long id) {
        ActivityVo activityVo = activityService.detail(id);
        if (activityVo.getStatus() != null && activityVo.getStatus() == 0) {
            return Result.error("活动已下架");
        }
        return Result.ok(activityVo);
    }
}

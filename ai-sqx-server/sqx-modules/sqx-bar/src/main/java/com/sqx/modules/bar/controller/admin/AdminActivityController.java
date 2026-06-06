package com.sqx.modules.bar.controller.admin;

import com.sqx.common.PageResult;
import com.sqx.common.Result;
import com.sqx.modules.bar.domain.bo.ActivityQueryBo;
import com.sqx.modules.bar.domain.bo.ActivitySaveBo;
import com.sqx.modules.bar.domain.vo.ActivityVo;
import com.sqx.modules.bar.service.BarActivityService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端 - 活动管理
 */
@RestController
@RequestMapping("/admin/activity")
public class AdminActivityController {

    private final BarActivityService activityService;

    public AdminActivityController(BarActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/list")
    @RequiresPermissions("bar:activity:list")
    public Result<PageResult<ActivityVo>> list(ActivityQueryBo queryBo) {
        return Result.ok(activityService.page(queryBo));
    }

    @PostMapping("/save")
    @RequiresPermissions("bar:activity:save")
    public Result<Void> save(@RequestBody ActivitySaveBo bo) {
        activityService.save(bo);
        return Result.ok();
    }

    @PutMapping("/update")
    @RequiresPermissions("bar:activity:update")
    public Result<Void> update(@RequestBody ActivitySaveBo bo) {
        activityService.update(bo);
        return Result.ok();
    }

    @DeleteMapping("/delete/{id}")
    @RequiresPermissions("bar:activity:delete")
    public Result<Void> delete(@PathVariable Long id) {
        activityService.delete(id);
        return Result.ok();
    }

    @PutMapping("/status/{id}")
    @RequiresPermissions("bar:activity:update")
    public Result<Void> updateStatus(@PathVariable Long id, Integer status) {
        activityService.updateStatus(id, status);
        return Result.ok();
    }
}

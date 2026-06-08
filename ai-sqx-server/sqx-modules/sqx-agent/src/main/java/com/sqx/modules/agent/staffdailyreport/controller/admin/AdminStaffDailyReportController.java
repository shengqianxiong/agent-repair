package com.sqx.modules.agent.staffdailyreport.controller.admin;

import com.sqx.common.PageResult;
import com.sqx.common.Result;
import com.sqx.modules.agent.base.AgentPageQueryBo;
import com.sqx.modules.agent.staffdailyreport.db.entity.StaffDailyReport;
import com.sqx.modules.agent.staffdailyreport.domain.bo.StaffDailyReportQueryBo;
import com.sqx.modules.agent.staffdailyreport.domain.bo.StaffDailyReportSaveBo;
import com.sqx.modules.agent.staffdailyreport.service.StaffDailyReportService;
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
 * 管理端 - 员工日报（软删除 + 回收站）
 */
@RestController
@RequestMapping("/admin/agent/staff-daily-report")
public class AdminStaffDailyReportController {

    private final StaffDailyReportService staffDailyReportService;

    public AdminStaffDailyReportController(StaffDailyReportService staffDailyReportService) {
        this.staffDailyReportService = staffDailyReportService;
    }

    @GetMapping("/list")
    @RequiresPermissions("agent:staffDailyReport:list")
    public Result<PageResult<StaffDailyReport>> list(StaffDailyReportQueryBo queryBo) {
        return Result.ok(staffDailyReportService.page(queryBo));
    }

    @GetMapping("/detail/{id}")
    @RequiresPermissions("agent:staffDailyReport:detail")
    public Result<StaffDailyReport> detail(@PathVariable Long id) {
        return Result.ok(staffDailyReportService.detail(id));
    }

    @PostMapping("/save")
    @RequiresPermissions("agent:staffDailyReport:save")
    public Result<Void> save(@RequestBody StaffDailyReportSaveBo bo) {
        staffDailyReportService.save(bo);
        return Result.ok();
    }

    @PutMapping("/update")
    @RequiresPermissions("agent:staffDailyReport:update")
    public Result<Void> update(@RequestBody StaffDailyReportSaveBo bo) {
        staffDailyReportService.update(bo);
        return Result.ok();
    }

    @DeleteMapping("/delete/{id}")
    @RequiresPermissions("agent:staffDailyReport:delete")
    public Result<Void> delete(@PathVariable Long id) {
        staffDailyReportService.delete(id);
        return Result.ok();
    }

    @GetMapping("/recycle/list")
    @RequiresPermissions("agent:staffDailyReport:recycle")
    public Result<PageResult<StaffDailyReport>> recycleList(AgentPageQueryBo queryBo) {
        return Result.ok(staffDailyReportService.recyclePage(queryBo));
    }

    @PutMapping("/restore/{id}")
    @RequiresPermissions("agent:staffDailyReport:restore")
    public Result<Void> restore(@PathVariable Long id) {
        staffDailyReportService.restore(id);
        return Result.ok();
    }

    @DeleteMapping("/permanent/{id}")
    @RequiresPermissions("agent:staffDailyReport:permanent")
    public Result<Void> permanentDelete(@PathVariable Long id) {
        staffDailyReportService.permanentDelete(id);
        return Result.ok();
    }
}

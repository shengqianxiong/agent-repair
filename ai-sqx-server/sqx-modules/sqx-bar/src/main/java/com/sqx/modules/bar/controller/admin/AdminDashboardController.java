package com.sqx.modules.bar.controller.admin;

import com.sqx.common.Result;
import com.sqx.modules.bar.domain.vo.BookingVo;
import com.sqx.modules.bar.domain.vo.DashboardStatsVo;
import com.sqx.modules.bar.domain.vo.OrderVo;
import com.sqx.modules.bar.service.BarDashboardService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理端 - 数据看板
 */
@RestController
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {

    private final BarDashboardService dashboardService;

    public AdminDashboardController(BarDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    @RequiresPermissions("bar:dashboard:stats")
    public Result<DashboardStatsVo> stats() {
        return Result.ok(dashboardService.stats());
    }

    @GetMapping("/recent-orders")
    @RequiresPermissions("bar:dashboard:stats")
    public Result<List<OrderVo>> recentOrders() {
        return Result.ok(dashboardService.recentOrders(10));
    }

    @GetMapping("/recent-bookings")
    @RequiresPermissions("bar:dashboard:stats")
    public Result<List<BookingVo>> recentBookings() {
        return Result.ok(dashboardService.recentBookings(10));
    }
}

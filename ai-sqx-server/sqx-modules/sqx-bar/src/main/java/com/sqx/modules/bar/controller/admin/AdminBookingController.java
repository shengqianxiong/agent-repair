package com.sqx.modules.bar.controller.admin;

import com.sqx.common.PageResult;
import com.sqx.common.Result;
import com.sqx.modules.bar.domain.bo.BookingAssignTableBo;
import com.sqx.modules.bar.domain.bo.BookingQueryBo;
import com.sqx.modules.bar.domain.vo.BookingVo;
import com.sqx.modules.bar.service.BarBookingService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端 - 预约管理
 */
@RestController
@RequestMapping("/admin/booking")
public class AdminBookingController {

    private final BarBookingService bookingService;

    public AdminBookingController(BarBookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/list")
    @RequiresPermissions("bar:booking:list")
    public Result<PageResult<BookingVo>> list(BookingQueryBo queryBo) {
        return Result.ok(bookingService.page(queryBo));
    }

    @GetMapping("/detail/{id}")
    @RequiresPermissions("bar:booking:detail")
    public Result<BookingVo> detail(@PathVariable Long id) {
        return Result.ok(bookingService.detail(id));
    }

    @PutMapping("/confirm/{id}")
    @RequiresPermissions("bar:booking:confirm")
    public Result<Void> confirm(@PathVariable Long id) {
        bookingService.confirm(id);
        return Result.ok();
    }

    @PutMapping("/cancel/{id}")
    @RequiresPermissions("bar:booking:cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        bookingService.adminCancel(id);
        return Result.ok();
    }

    @PutMapping("/assign-table")
    @RequiresPermissions("bar:booking:assign")
    public Result<Void> assignTable(@RequestBody BookingAssignTableBo bo) {
        bookingService.assignTable(bo);
        return Result.ok();
    }
}

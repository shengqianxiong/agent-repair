package com.sqx.modules.bar.controller.app;

import com.sqx.common.PageResult;
import com.sqx.common.Result;
import com.sqx.common.annotation.Login;
import com.sqx.common.utils.UserContext;
import com.sqx.modules.bar.domain.bo.BookingQueryBo;
import com.sqx.modules.bar.domain.bo.BookingSubmitBo;
import com.sqx.modules.bar.domain.vo.BookingVo;
import com.sqx.modules.bar.domain.vo.TimeSlotVo;
import com.sqx.modules.bar.service.BarBookingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户端 - 预约
 */
@RestController
@RequestMapping("/app/booking")
public class AppBookingController {

    private final BarBookingService bookingService;

    public AppBookingController(BarBookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/time-slots")
    @Login
    public Result<List<TimeSlotVo>> timeSlots() {
        return Result.ok(bookingService.timeSlots());
    }

    @PostMapping("/submit")
    @Login
    public Result<BookingVo> submit(@RequestBody BookingSubmitBo bo) {
        return Result.ok(bookingService.submit(UserContext.getUserId(), bo));
    }

    @GetMapping("/list")
    @Login
    public Result<PageResult<BookingVo>> list(BookingQueryBo queryBo) {
        queryBo.setUserId(UserContext.getUserId());
        return Result.ok(bookingService.page(queryBo));
    }

    @GetMapping("/detail/{id}")
    @Login
    public Result<BookingVo> detail(@PathVariable Long id) {
        BookingVo bookingVo = bookingService.detail(id);
        if (!bookingVo.getUserId().equals(UserContext.getUserId())) {
            return Result.error("无权查看该预约");
        }
        return Result.ok(bookingVo);
    }

    @PutMapping("/cancel/{id}")
    @Login
    public Result<Void> cancel(@PathVariable Long id) {
        bookingService.cancel(UserContext.getUserId(), id);
        return Result.ok();
    }
}

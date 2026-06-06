package com.sqx.modules.bar.service.impl;

import com.sqx.modules.bar.domain.vo.ActivityVo;
import com.sqx.modules.bar.domain.vo.HomeIndexVo;
import com.sqx.modules.bar.domain.vo.QuickActionVo;
import com.sqx.modules.bar.domain.vo.UserVo;
import com.sqx.modules.bar.service.BarActivityService;
import com.sqx.modules.bar.service.BarHomeService;
import com.sqx.modules.bar.service.BarUserService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class BarHomeServiceImpl implements BarHomeService {

    private final BarUserService userService;
    private final BarActivityService activityService;

    public BarHomeServiceImpl(BarUserService userService, BarActivityService activityService) {
        this.userService = userService;
        this.activityService = activityService;
    }

    @Override
    public HomeIndexVo index(Long userId) {
        HomeIndexVo vo = new HomeIndexVo();
        UserVo user = userService.info(userId);
        vo.setUser(user);
        vo.setPoints(user.getPoints());
        List<ActivityVo> activities = activityService.listEnabled(5);
        vo.setActivities(activities);
        vo.setQuickActions(buildQuickActions());
        return vo;
    }

    private List<QuickActionVo> buildQuickActions() {
        QuickActionVo order = new QuickActionVo();
        order.setName("自助点餐");
        order.setIcon("order");
        order.setPath("/pages/order/index");

        QuickActionVo booking = new QuickActionVo();
        booking.setName("在线预约");
        booking.setIcon("calendar");
        booking.setPath("/pages/booking/index");

        return Arrays.asList(order, booking);
    }
}

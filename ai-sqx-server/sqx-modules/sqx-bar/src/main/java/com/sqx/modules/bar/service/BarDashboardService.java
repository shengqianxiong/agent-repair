package com.sqx.modules.bar.service;

import com.sqx.modules.bar.domain.vo.BookingVo;
import com.sqx.modules.bar.domain.vo.DashboardStatsVo;
import com.sqx.modules.bar.domain.vo.OrderVo;

import java.util.List;

public interface BarDashboardService {

    DashboardStatsVo stats();

    List<OrderVo> recentOrders(int limit);

    List<BookingVo> recentBookings(int limit);
}

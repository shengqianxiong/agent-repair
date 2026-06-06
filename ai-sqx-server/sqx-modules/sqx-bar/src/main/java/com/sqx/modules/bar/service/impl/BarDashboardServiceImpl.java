package com.sqx.modules.bar.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.modules.bar.constant.BarConstants;
import com.sqx.modules.bar.db.entity.BarOrder;
import com.sqx.modules.bar.db.mapper.BarBookingMapper;
import com.sqx.modules.bar.db.mapper.BarOrderMapper;
import com.sqx.modules.bar.db.mapper.BarUserMapper;
import com.sqx.modules.bar.domain.vo.BookingVo;
import com.sqx.modules.bar.domain.vo.DashboardStatsVo;
import com.sqx.modules.bar.domain.vo.OrderVo;
import com.sqx.modules.bar.service.BarBookingService;
import com.sqx.modules.bar.service.BarDashboardService;
import com.sqx.modules.bar.service.BarOrderService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service
public class BarDashboardServiceImpl implements BarDashboardService {

    private final BarOrderMapper orderMapper;
    private final BarBookingMapper bookingMapper;
    private final BarUserMapper userMapper;
    private final BarOrderService orderService;
    private final BarBookingService bookingService;

    public BarDashboardServiceImpl(BarOrderMapper orderMapper,
                                   BarBookingMapper bookingMapper,
                                   BarUserMapper userMapper,
                                   BarOrderService orderService,
                                   BarBookingService bookingService) {
        this.orderMapper = orderMapper;
        this.bookingMapper = bookingMapper;
        this.userMapper = userMapper;
        this.orderService = orderService;
        this.bookingService = bookingService;
    }

    @Override
    public DashboardStatsVo stats() {
        DashboardStatsVo vo = new DashboardStatsVo();
        vo.setOrderCount(orderMapper.selectCount(null));
        vo.setBookingCount(bookingMapper.selectCount(null));
        vo.setMemberCount(userMapper.selectCount(null));
        List<BarOrder> paidOrders = orderMapper.selectList(new LambdaQueryWrapper<BarOrder>()
                .in(BarOrder::getStatus, Arrays.asList(
                        BarConstants.ORDER_STATUS_PAID,
                        BarConstants.ORDER_STATUS_PREPARING,
                        BarConstants.ORDER_STATUS_COMPLETED)));
        BigDecimal sales = paidOrders.stream()
                .map(BarOrder::getPayAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setSalesAmount(sales);
        return vo;
    }

    @Override
    public List<OrderVo> recentOrders(int limit) {
        return orderService.recent(limit);
    }

    @Override
    public List<BookingVo> recentBookings(int limit) {
        return bookingService.recent(limit);
    }
}

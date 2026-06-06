package com.sqx.modules.bar.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DashboardStatsVo {

    private Long orderCount;
    private Long bookingCount;
    private BigDecimal salesAmount;
    private Long memberCount;
}

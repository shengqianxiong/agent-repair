package com.sqx.modules.review.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserSummaryVo {
    private BigDecimal totalRebate;
    private BigDecimal paidAmount;
    private Integer pendingCount;
    private Integer taskCount;
    private Long latestRebateId;
}

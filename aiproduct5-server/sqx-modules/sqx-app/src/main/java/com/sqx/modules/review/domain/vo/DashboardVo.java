package com.sqx.modules.review.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DashboardVo {
    private Integer activityCount;
    private Integer scanCount;
    private Integer submitCount;
    private Integer verifyCount;
    private BigDecimal rebateExpense;
}

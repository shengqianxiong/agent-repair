package com.sqx.modules.review.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RebateVo {
    private Long rebateId;
    private Long id;
    private Long taskId;
    private Long activityId;
    private String activityTitle;
    private String productName;
    private String productImage;
    private BigDecimal amount;
    private BigDecimal rebateAmount;
    private String status;
    private String statusText;
    private String paidTime;
    private String screenshotUrl;
    private String reviewImage;
    private String rejectReason;
    private TaskVo taskInfo;
}

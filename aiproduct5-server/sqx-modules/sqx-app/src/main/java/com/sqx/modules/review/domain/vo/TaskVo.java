package com.sqx.modules.review.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TaskVo {
    private Long taskId;
    private Long id;
    private Long activityId;
    private String activityTitle;
    private String productName;
    private String productImage;
    private String userIdentifier;
    private String status;
    private String statusText;
    private BigDecimal rebateAmount;
    private String screenshotUrl;
    private String remark;
    private String contact;
    private String submitTime;
    private String paidTime;
    private String rejectReason;
    private Long rebateId;
    private ActivityVo activityInfo;
}

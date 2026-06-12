package com.sqx.modules.review.domain.bo;

import lombok.Data;

@Data
public class TaskSubmitBo {
    private Long taskId;
    private Long activityId;
    private String screenshotUrl;
    private String remark;
    private String contact;
    private String userIdentifier;
}

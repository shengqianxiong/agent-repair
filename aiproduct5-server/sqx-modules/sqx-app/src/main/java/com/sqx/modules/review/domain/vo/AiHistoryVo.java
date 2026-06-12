package com.sqx.modules.review.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class AiHistoryVo {
    private Long activityId;
    private String activityTitle;
    private String status;
    private String failReason;
    private List<CopywritingVo> copywritingList;
    private List<String> recommendImages;
    private String createTime;
}

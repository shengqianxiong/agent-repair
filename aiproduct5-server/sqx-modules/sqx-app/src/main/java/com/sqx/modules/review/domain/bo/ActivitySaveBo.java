package com.sqx.modules.review.domain.bo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ActivitySaveBo {
    private Long id;
    private String productUrl;
    private String title;
    private String productName;
    private String productImage;
    private String shopName;
    private String copywriting;
    private List<CopywritingItemBo> copywritingList;
    private String imageUrl;
    private List<String> recommendImages;
    private BigDecimal rewardAmount;
    private BigDecimal rebateAmount;
    private String expireTime;
    private String validEndTime;
    private String douyinLink;
    private Integer status;
}

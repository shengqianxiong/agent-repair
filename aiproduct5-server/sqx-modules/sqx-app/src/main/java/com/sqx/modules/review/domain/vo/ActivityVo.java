package com.sqx.modules.review.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ActivityVo {
    private Long id;
    private String code;
    private String title;
    private String productName;
    private String productImage;
    private String shopName;
    private String productUrl;
    private BigDecimal rebateAmount;
    private String validEndTime;
    private String douyinLink;
    private List<CopywritingVo> copywritingList;
    private List<String> recommendImages;
    private Integer status;
    private Integer scanCount;
    private Integer submitCount;
    private Integer verifyCount;
    private String qrcodeUrl;
}

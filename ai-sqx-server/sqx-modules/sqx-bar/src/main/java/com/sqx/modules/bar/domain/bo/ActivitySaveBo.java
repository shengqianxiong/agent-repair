package com.sqx.modules.bar.domain.bo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ActivitySaveBo {

    private Long id;
    private String title;
    private String image;
    private String tag;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Date startTime;
    private Date endTime;
    private Integer status;
}

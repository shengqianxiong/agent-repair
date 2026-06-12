package com.sqx.modules.review.domain.bo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ActivityQueryBo {
    private String keyword;
    private Integer status;
    private Integer page;
    private Integer pageSize;
}

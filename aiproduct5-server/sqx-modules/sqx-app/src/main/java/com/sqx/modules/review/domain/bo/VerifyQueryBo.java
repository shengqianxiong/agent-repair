package com.sqx.modules.review.domain.bo;

import lombok.Data;

@Data
public class VerifyQueryBo {
    private String keyword;
    private String status;
    private Integer page;
    private Integer pageSize;
}

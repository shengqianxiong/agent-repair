package com.sqx.modules.review.domain.bo;

import lombok.Data;

@Data
public class VerifyActionBo {
    private Long verifyId;
    private Long id;
    private String remark;
    private String reason;
}

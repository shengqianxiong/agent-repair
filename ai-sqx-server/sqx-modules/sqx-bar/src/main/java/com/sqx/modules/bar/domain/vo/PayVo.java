package com.sqx.modules.bar.domain.vo;

import lombok.Data;

@Data
public class PayVo {

    private Long orderId;
    private String orderNo;
    private String paySign;
    private String timeStamp;
    private String nonceStr;
    private String packageValue;
}

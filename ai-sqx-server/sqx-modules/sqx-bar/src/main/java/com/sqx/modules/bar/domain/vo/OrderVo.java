package com.sqx.modules.bar.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class OrderVo {

    private Long id;
    private String orderNo;
    private Long userId;
    private String userNickname;
    private Long tableId;
    private String tableNo;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private String status;
    private String remark;
    private Date createTime;
    private Date payTime;
    private List<OrderItemVo> items;
}

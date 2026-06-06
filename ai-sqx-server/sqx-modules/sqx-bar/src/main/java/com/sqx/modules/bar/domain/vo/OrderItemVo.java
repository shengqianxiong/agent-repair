package com.sqx.modules.bar.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemVo {

    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}

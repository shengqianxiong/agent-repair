package com.sqx.modules.bar.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartVo {

    private Long id;
    private Long productId;
    private String productName;
    private String image;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
}

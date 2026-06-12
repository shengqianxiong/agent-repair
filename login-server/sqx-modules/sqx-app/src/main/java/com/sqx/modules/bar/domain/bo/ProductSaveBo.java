package com.sqx.modules.bar.domain.bo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductSaveBo {

    private Long id;
    private String name;
    private Long categoryId;
    private String image;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String description;
    private Integer stock;
    private Integer status;
}

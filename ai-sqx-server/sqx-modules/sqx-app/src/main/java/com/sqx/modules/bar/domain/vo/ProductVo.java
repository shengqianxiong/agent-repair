package com.sqx.modules.bar.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ProductVo {

    private Long id;
    private String name;
    private Long categoryId;
    private String categoryName;
    private String image;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String description;
    private Integer stock;
    private Integer status;
    private Date createTime;
}

package com.sqx.modules.agent.taogoods.domain.bo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TaoGoodsSaveBo {

    private Long id;
    private String name;
    private Long categoryId;
    private String image;
    private BigDecimal price;
    private String description;
    private Integer status;
}

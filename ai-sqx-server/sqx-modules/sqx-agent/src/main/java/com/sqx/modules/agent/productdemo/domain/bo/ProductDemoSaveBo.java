package com.sqx.modules.agent.productdemo.domain.bo;

import lombok.Data;

@Data
public class ProductDemoSaveBo {

    private Long id;
    private String name;
    private Long productId;
    private String demoUrl;
    private String content;
    private Integer status;
}

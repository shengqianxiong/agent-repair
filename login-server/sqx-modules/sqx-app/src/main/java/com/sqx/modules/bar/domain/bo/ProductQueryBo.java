package com.sqx.modules.bar.domain.bo;

import lombok.Data;

@Data
public class ProductQueryBo {

    private String name;
    private Long categoryId;
    private Integer status;
    private Integer page = 1;
    private Integer pageSize = 10;
}

package com.sqx.modules.bar.domain.bo;

import lombok.Data;

@Data
public class CategorySaveBo {

    private Long id;
    private String name;
    private Integer sort;
    private Integer status;
}

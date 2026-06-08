package com.sqx.modules.agent.category.domain.bo;

import lombok.Data;

@Data
public class CategorySaveBo {

    private Long id;
    private String name;
    private Long parentId;
    private Integer sort;
    private Integer status;
}

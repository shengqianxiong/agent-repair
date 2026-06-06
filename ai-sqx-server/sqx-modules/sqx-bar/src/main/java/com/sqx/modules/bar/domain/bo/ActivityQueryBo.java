package com.sqx.modules.bar.domain.bo;

import lombok.Data;

@Data
public class ActivityQueryBo {

    private Integer page;
    private Integer pageSize;
    private String title;
    private Integer status;
}

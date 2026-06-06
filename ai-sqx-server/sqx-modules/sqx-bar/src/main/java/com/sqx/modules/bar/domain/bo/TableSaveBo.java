package com.sqx.modules.bar.domain.bo;

import lombok.Data;

@Data
public class TableSaveBo {

    private Long id;
    private String tableNo;
    private String area;
    private Integer capacity;
    private String status;
}

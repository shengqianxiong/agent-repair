package com.sqx.modules.bar.domain.bo;

import lombok.Data;

@Data
public class OrderQueryBo {

    private Integer page;
    private Integer pageSize;
    private String status;
    private String orderNo;
    private Long userId;
}

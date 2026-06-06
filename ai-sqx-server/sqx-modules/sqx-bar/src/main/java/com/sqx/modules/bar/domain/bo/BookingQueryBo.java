package com.sqx.modules.bar.domain.bo;

import lombok.Data;

import java.util.Date;

@Data
public class BookingQueryBo {

    private Integer page;
    private Integer pageSize;
    private String status;
    private Date bookingDate;
    private Long userId;
}

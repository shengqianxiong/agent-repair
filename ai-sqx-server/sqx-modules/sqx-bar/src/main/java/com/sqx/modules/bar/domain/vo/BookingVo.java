package com.sqx.modules.bar.domain.vo;

import lombok.Data;

import java.util.Date;

@Data
public class BookingVo {

    private Long id;
    private Long userId;
    private String userNickname;
    private Date bookingDate;
    private String timeSlot;
    private Integer guestCount;
    private String seatType;
    private Long tableId;
    private String tableNo;
    private String status;
    private Date createTime;
}

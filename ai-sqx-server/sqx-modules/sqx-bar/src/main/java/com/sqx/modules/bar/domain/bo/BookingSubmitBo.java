package com.sqx.modules.bar.domain.bo;

import lombok.Data;

import java.util.Date;

@Data
public class BookingSubmitBo {

    private Date bookingDate;
    private String timeSlot;
    private Integer guestCount;
    private String seatType;
}

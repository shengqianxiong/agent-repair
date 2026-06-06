package com.sqx.modules.bar.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 桌位预约
 */
@Data
@TableName("bar_booking")
public class BarBooking implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Date bookingDate;
    private String timeSlot;
    private Integer guestCount;
    private String seatType;
    private Long tableId;
    /** 待确认/已确认/已到店/已取消 */
    private String status;
    private Date createTime;
    private Date updateTime;
}

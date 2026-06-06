package com.sqx.modules.bar.constant;

/**
 * 酒吧业务常量
 */
public final class BarConstants {

    private BarConstants() {
    }

    public static final String ORDER_STATUS_PENDING = "待支付";
    public static final String ORDER_STATUS_PAID = "已支付";
    public static final String ORDER_STATUS_PREPARING = "制作中";
    public static final String ORDER_STATUS_COMPLETED = "已完成";
    public static final String ORDER_STATUS_CANCELLED = "已取消";

    public static final String BOOKING_STATUS_PENDING = "待确认";
    public static final String BOOKING_STATUS_CONFIRMED = "已确认";
    public static final String BOOKING_STATUS_ARRIVED = "已到店";
    public static final String BOOKING_STATUS_CANCELLED = "已取消";

    public static final String TABLE_STATUS_FREE = "空闲";
    public static final String TABLE_STATUS_OCCUPIED = "占用";
    public static final String TABLE_STATUS_RESERVED = "预约中";
}

package com.sqx.modules.bar.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 点餐订单
 */
@Data
@TableName("bar_order")
public class BarOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long userId;
    private Long tableId;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    /** 待支付/已支付/制作中/已完成/已取消 */
    private String status;
    private String remark;
    private Date createTime;
    private Date payTime;
}

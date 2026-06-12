package com.sqx.modules.review.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("review_rebate")
public class ReviewRebate implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long taskId;
    private Long activityId;
    private String userIdentifier;
    private BigDecimal amount;
    /** auditing / paid / rejected */
    private String status;
    private Date paidTime;
    private String rejectReason;
    private Date createTime;
    private Date updateTime;
}

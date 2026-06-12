package com.sqx.modules.review.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("review_task")
public class ReviewTask implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long activityId;
    private String userIdentifier;
    /** started / auditing / paid / rejected */
    private String status;
    private Long copyTextId;
    private Integer copyReported;
    private String screenshotUrl;
    private String remark;
    private String contact;
    private Date createTime;
    private Date updateTime;
}

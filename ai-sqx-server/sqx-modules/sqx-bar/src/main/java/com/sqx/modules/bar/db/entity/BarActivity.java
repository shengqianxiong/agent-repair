package com.sqx.modules.bar.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 精选活动
 */
@Data
@TableName("bar_activity")
public class BarActivity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String image;
    private String tag;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Date startTime;
    private Date endTime;
    /** 0-下架 1-上架 */
    private Integer status;
    private Date createTime;
    private Date updateTime;
}

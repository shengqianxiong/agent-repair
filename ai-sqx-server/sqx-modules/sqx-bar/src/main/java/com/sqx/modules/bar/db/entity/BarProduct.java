package com.sqx.modules.bar.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 酒水商品
 */
@Data
@TableName("bar_product")
public class BarProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Long categoryId;
    private String image;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String description;
    private Integer stock;
    /** 0-下架 1-上架 */
    private Integer status;
    private Date createTime;
    private Date updateTime;
}

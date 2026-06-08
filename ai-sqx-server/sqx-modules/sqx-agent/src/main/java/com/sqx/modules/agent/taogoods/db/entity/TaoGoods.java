package com.sqx.modules.agent.taogoods.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sqx.modules.agent.base.BaseAgentEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 淘商品
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tao_goods")
public class TaoGoods extends BaseAgentEntity {

    private String name;
    private Long categoryId;
    private String image;
    private BigDecimal price;
    private String description;
    /** 0-下架 1-上架 */
    private Integer status;
}

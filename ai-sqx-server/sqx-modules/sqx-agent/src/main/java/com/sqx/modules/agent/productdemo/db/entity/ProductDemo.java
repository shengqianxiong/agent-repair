package com.sqx.modules.agent.productdemo.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sqx.modules.agent.base.BaseAgentEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 产品演示
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product_demo")
public class ProductDemo extends BaseAgentEntity {

    private String name;
    private Long productId;
    private String demoUrl;
    private String content;
    /** 0-禁用 1-启用 */
    private Integer status;
}

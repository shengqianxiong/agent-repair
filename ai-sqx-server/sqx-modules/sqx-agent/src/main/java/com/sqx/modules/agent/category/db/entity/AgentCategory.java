package com.sqx.modules.agent.category.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sqx.modules.agent.base.BaseAgentEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品分类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("agent_category")
public class AgentCategory extends BaseAgentEntity {

    private String name;
    private Long parentId;
    private Integer sort;
    /** 0-禁用 1-启用 */
    private Integer status;
}

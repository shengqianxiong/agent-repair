package com.sqx.modules.agent.userlink.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sqx.modules.agent.base.BaseAgentEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户链接
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_link")
public class UserLink extends BaseAgentEntity {

    private Long userId;
    private String linkType;
    private String linkUrl;
    private String title;
    /** 0-禁用 1-启用 */
    private Integer status;
}

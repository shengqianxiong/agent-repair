package com.sqx.modules.agent.employee.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sqx.modules.agent.base.BaseAgentEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 员工
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("agent_employee")
public class AgentEmployee extends BaseAgentEntity {

    private String name;
    private String phone;
    private String position;
    private String department;
    private String avatar;
    /** 0-离职 1-在职 */
    private Integer status;
}

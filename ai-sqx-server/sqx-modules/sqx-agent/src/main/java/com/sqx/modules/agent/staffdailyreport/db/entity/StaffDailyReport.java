package com.sqx.modules.agent.staffdailyreport.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sqx.modules.agent.base.BaseAgentEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 员工日报
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("staff_daily_report")
public class StaffDailyReport extends BaseAgentEntity {

    private Long employeeId;
    private Date reportDate;
    private String content;
    /** 0-草稿 1-已提交 */
    private Integer status;
}

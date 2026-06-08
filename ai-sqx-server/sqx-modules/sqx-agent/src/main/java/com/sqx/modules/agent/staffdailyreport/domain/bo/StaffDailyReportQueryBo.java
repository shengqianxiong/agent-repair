package com.sqx.modules.agent.staffdailyreport.domain.bo;

import com.sqx.modules.agent.base.AgentPageQueryBo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class StaffDailyReportQueryBo extends AgentPageQueryBo {

    private Long employeeId;
}

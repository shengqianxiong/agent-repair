package com.sqx.modules.agent.staffdailyreport.service;

import com.sqx.common.PageResult;
import com.sqx.modules.agent.base.AgentPageQueryBo;
import com.sqx.modules.agent.staffdailyreport.db.entity.StaffDailyReport;
import com.sqx.modules.agent.staffdailyreport.domain.bo.StaffDailyReportQueryBo;
import com.sqx.modules.agent.staffdailyreport.domain.bo.StaffDailyReportSaveBo;

public interface StaffDailyReportService {

    PageResult<StaffDailyReport> page(StaffDailyReportQueryBo queryBo);

    StaffDailyReport detail(Long id);

    void save(StaffDailyReportSaveBo bo);

    void update(StaffDailyReportSaveBo bo);

    void delete(Long id);

    PageResult<StaffDailyReport> recyclePage(AgentPageQueryBo queryBo);

    void restore(Long id);

    void permanentDelete(Long id);
}

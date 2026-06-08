package com.sqx.modules.agent.staffdailyreport.domain.bo;

import lombok.Data;

import java.util.Date;

@Data
public class StaffDailyReportSaveBo {

    private Long id;
    private Long employeeId;
    private Date reportDate;
    private String content;
    private Integer status;
}

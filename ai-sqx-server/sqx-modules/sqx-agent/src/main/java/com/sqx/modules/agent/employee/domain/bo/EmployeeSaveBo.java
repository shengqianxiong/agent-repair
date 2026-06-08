package com.sqx.modules.agent.employee.domain.bo;

import lombok.Data;

@Data
public class EmployeeSaveBo {

    private Long id;
    private String name;
    private String phone;
    private String position;
    private String department;
    private String avatar;
    private Integer status;
}

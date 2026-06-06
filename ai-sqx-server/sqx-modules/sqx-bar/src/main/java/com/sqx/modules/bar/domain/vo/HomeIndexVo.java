package com.sqx.modules.bar.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class HomeIndexVo {

    private UserVo user;
    private Integer points;
    private List<ActivityVo> activities;
    private List<QuickActionVo> quickActions;
}

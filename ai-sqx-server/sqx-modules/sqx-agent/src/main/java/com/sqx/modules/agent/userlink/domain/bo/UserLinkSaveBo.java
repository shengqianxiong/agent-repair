package com.sqx.modules.agent.userlink.domain.bo;

import lombok.Data;

@Data
public class UserLinkSaveBo {

    private Long id;
    private Long userId;
    private String linkType;
    private String linkUrl;
    private String title;
    private Integer status;
}

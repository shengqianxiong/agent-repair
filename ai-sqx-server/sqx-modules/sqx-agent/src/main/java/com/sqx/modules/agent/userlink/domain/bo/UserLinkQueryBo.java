package com.sqx.modules.agent.userlink.domain.bo;

import com.sqx.modules.agent.base.AgentPageQueryBo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserLinkQueryBo extends AgentPageQueryBo {

    private Long userId;
    private String linkType;
}

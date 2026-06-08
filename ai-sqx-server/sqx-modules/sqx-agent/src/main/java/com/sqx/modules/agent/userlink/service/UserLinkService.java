package com.sqx.modules.agent.userlink.service;

import com.sqx.common.PageResult;
import com.sqx.modules.agent.base.AgentPageQueryBo;
import com.sqx.modules.agent.userlink.db.entity.UserLink;
import com.sqx.modules.agent.userlink.domain.bo.UserLinkQueryBo;
import com.sqx.modules.agent.userlink.domain.bo.UserLinkSaveBo;

public interface UserLinkService {

    PageResult<UserLink> page(UserLinkQueryBo queryBo);

    UserLink detail(Long id);

    void save(UserLinkSaveBo bo);

    void update(UserLinkSaveBo bo);

    void delete(Long id);

    PageResult<UserLink> recyclePage(AgentPageQueryBo queryBo);

    void restore(Long id);

    void permanentDelete(Long id);
}

package com.sqx.modules.agent.taogoods.service;

import com.sqx.common.PageResult;
import com.sqx.modules.agent.base.AgentPageQueryBo;
import com.sqx.modules.agent.taogoods.db.entity.TaoGoods;
import com.sqx.modules.agent.taogoods.domain.bo.TaoGoodsSaveBo;

public interface TaoGoodsService {

    PageResult<TaoGoods> page(AgentPageQueryBo queryBo);

    TaoGoods detail(Long id);

    void save(TaoGoodsSaveBo bo);

    void update(TaoGoodsSaveBo bo);

    void delete(Long id);

    PageResult<TaoGoods> recyclePage(AgentPageQueryBo queryBo);

    void restore(Long id);

    void permanentDelete(Long id);
}

package com.sqx.modules.agent.category.service;

import com.sqx.common.PageResult;
import com.sqx.modules.agent.base.AgentPageQueryBo;
import com.sqx.modules.agent.category.db.entity.AgentCategory;
import com.sqx.modules.agent.category.domain.bo.CategorySaveBo;

import java.util.List;

public interface AgentCategoryService {

    PageResult<AgentCategory> page(AgentPageQueryBo queryBo);

    List<AgentCategory> listAll();

    AgentCategory detail(Long id);

    void save(CategorySaveBo bo);

    void update(CategorySaveBo bo);

    void delete(Long id);

    PageResult<AgentCategory> recyclePage(AgentPageQueryBo queryBo);

    void restore(Long id);

    void permanentDelete(Long id);
}

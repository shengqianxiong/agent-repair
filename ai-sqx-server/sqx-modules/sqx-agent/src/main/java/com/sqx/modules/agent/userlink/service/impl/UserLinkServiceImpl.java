package com.sqx.modules.agent.userlink.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.common.PageResult;
import com.sqx.common.exception.BusinessException;
import com.sqx.modules.agent.base.AbstractAgentService;
import com.sqx.modules.agent.base.AgentPageQueryBo;
import com.sqx.modules.agent.base.RecycleBinSupport;
import com.sqx.modules.agent.userlink.db.entity.UserLink;
import com.sqx.modules.agent.userlink.db.mapper.UserLinkMapper;
import com.sqx.modules.agent.userlink.domain.bo.UserLinkQueryBo;
import com.sqx.modules.agent.userlink.domain.bo.UserLinkSaveBo;
import com.sqx.modules.agent.userlink.service.UserLinkService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserLinkServiceImpl extends AbstractAgentService<UserLink, UserLinkMapper> implements UserLinkService {

    public UserLinkServiceImpl(UserLinkMapper mapper, RecycleBinSupport recycleBinSupport) {
        super(mapper, recycleBinSupport);
    }

    @Override
    protected String tableName() {
        return "user_link";
    }

    @Override
    protected Class<UserLink> entityClass() {
        return UserLink.class;
    }

    @Override
    protected String entityLabel() {
        return "用户链接";
    }

    @Override
    protected com.baomidou.mybatisplus.core.toolkit.support.SFunction<UserLink, Date> createTimeColumn() {
        return UserLink::getCreateTime;
    }

    @Override
    public PageResult<UserLink> page(UserLinkQueryBo queryBo) {
        LambdaQueryWrapper<UserLink> wrapper = new LambdaQueryWrapper<>();
        if (queryBo.getUserId() != null) {
            wrapper.eq(UserLink::getUserId, queryBo.getUserId());
        }
        if (StrUtil.isNotBlank(queryBo.getLinkType())) {
            wrapper.eq(UserLink::getLinkType, queryBo.getLinkType());
        }
        if (StrUtil.isNotBlank(queryBo.getKeyword())) {
            wrapper.like(UserLink::getTitle, queryBo.getKeyword());
        }
        if (queryBo.getStatus() != null) {
            wrapper.eq(UserLink::getStatus, queryBo.getStatus());
        }
        return pageQuery(queryBo, wrapper);
    }

    @Override
    public UserLink detail(Long id) {
        return getOrThrow(id);
    }

    @Override
    public void save(UserLinkSaveBo bo) {
        validateSaveBo(bo, false);
        UserLink entity = new UserLink();
        entity.setUserId(bo.getUserId());
        entity.setLinkType(bo.getLinkType());
        entity.setLinkUrl(bo.getLinkUrl());
        entity.setTitle(bo.getTitle());
        entity.setStatus(bo.getStatus() == null ? 1 : bo.getStatus());
        fillCreateTime(entity);
        mapper.insert(entity);
    }

    @Override
    public void update(UserLinkSaveBo bo) {
        if (bo.getId() == null) {
            throw new BusinessException("用户链接ID不能为空");
        }
        validateSaveBo(bo, true);
        UserLink entity = getOrThrow(bo.getId());
        if (bo.getUserId() != null) {
            entity.setUserId(bo.getUserId());
        }
        entity.setLinkType(bo.getLinkType());
        entity.setLinkUrl(bo.getLinkUrl());
        entity.setTitle(bo.getTitle());
        if (bo.getStatus() != null) {
            entity.setStatus(bo.getStatus());
        }
        touchUpdateTime(entity);
        mapper.updateById(entity);
    }

    @Override
    public void delete(Long id) {
        softDelete(id);
    }

    private void validateSaveBo(UserLinkSaveBo bo, boolean isUpdate) {
        if (!isUpdate && bo.getId() != null) {
            throw new BusinessException("新增用户链接不能携带ID");
        }
        if (bo.getUserId() == null) {
            throw new BusinessException("用户ID不能为空");
        }
        if (StrUtil.isBlank(bo.getLinkUrl())) {
            throw new BusinessException("链接地址不能为空");
        }
    }
}

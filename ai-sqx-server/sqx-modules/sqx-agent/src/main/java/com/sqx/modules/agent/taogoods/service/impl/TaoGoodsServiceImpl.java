package com.sqx.modules.agent.taogoods.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.common.PageResult;
import com.sqx.common.exception.BusinessException;
import com.sqx.modules.agent.base.AbstractAgentService;
import com.sqx.modules.agent.base.AgentPageQueryBo;
import com.sqx.modules.agent.base.RecycleBinSupport;
import com.sqx.modules.agent.taogoods.db.entity.TaoGoods;
import com.sqx.modules.agent.taogoods.db.mapper.TaoGoodsMapper;
import com.sqx.modules.agent.taogoods.domain.bo.TaoGoodsSaveBo;
import com.sqx.modules.agent.taogoods.service.TaoGoodsService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TaoGoodsServiceImpl extends AbstractAgentService<TaoGoods, TaoGoodsMapper> implements TaoGoodsService {

    public TaoGoodsServiceImpl(TaoGoodsMapper mapper, RecycleBinSupport recycleBinSupport) {
        super(mapper, recycleBinSupport);
    }

    @Override
    protected String tableName() {
        return "tao_goods";
    }

    @Override
    protected Class<TaoGoods> entityClass() {
        return TaoGoods.class;
    }

    @Override
    protected String entityLabel() {
        return "淘商品";
    }

    @Override
    protected com.baomidou.mybatisplus.core.toolkit.support.SFunction<TaoGoods, Date> createTimeColumn() {
        return TaoGoods::getCreateTime;
    }

    @Override
    public PageResult<TaoGoods> page(AgentPageQueryBo queryBo) {
        LambdaQueryWrapper<TaoGoods> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(queryBo.getKeyword())) {
            wrapper.like(TaoGoods::getName, queryBo.getKeyword());
        }
        if (queryBo.getStatus() != null) {
            wrapper.eq(TaoGoods::getStatus, queryBo.getStatus());
        }
        return pageQuery(queryBo, wrapper);
    }

    @Override
    public TaoGoods detail(Long id) {
        return getOrThrow(id);
    }

    @Override
    public void save(TaoGoodsSaveBo bo) {
        validateSaveBo(bo, false);
        TaoGoods entity = new TaoGoods();
        BeanUtil.copyProperties(bo, entity);
        entity.setStatus(bo.getStatus() == null ? 1 : bo.getStatus());
        fillCreateTime(entity);
        mapper.insert(entity);
    }

    @Override
    public void update(TaoGoodsSaveBo bo) {
        if (bo.getId() == null) {
            throw new BusinessException("淘商品ID不能为空");
        }
        validateSaveBo(bo, true);
        TaoGoods entity = getOrThrow(bo.getId());
        entity.setName(bo.getName());
        entity.setCategoryId(bo.getCategoryId());
        entity.setImage(bo.getImage());
        entity.setPrice(bo.getPrice());
        entity.setDescription(bo.getDescription());
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

    private void validateSaveBo(TaoGoodsSaveBo bo, boolean isUpdate) {
        if (!isUpdate && bo.getId() != null) {
            throw new BusinessException("新增淘商品不能携带ID");
        }
        if (StrUtil.isBlank(bo.getName())) {
            throw new BusinessException("淘商品名称不能为空");
        }
        if (bo.getPrice() == null) {
            throw new BusinessException("淘商品价格不能为空");
        }
    }
}

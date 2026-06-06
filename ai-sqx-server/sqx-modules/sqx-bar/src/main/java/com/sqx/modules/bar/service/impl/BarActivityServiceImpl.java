package com.sqx.modules.bar.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.common.PageResult;
import com.sqx.common.exception.BusinessException;
import com.sqx.modules.bar.db.entity.BarActivity;
import com.sqx.modules.bar.db.mapper.BarActivityMapper;
import com.sqx.modules.bar.domain.bo.ActivityQueryBo;
import com.sqx.modules.bar.domain.bo.ActivitySaveBo;
import com.sqx.modules.bar.domain.vo.ActivityVo;
import com.sqx.modules.bar.service.BarActivityService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BarActivityServiceImpl implements BarActivityService {

    private final BarActivityMapper activityMapper;

    public BarActivityServiceImpl(BarActivityMapper activityMapper) {
        this.activityMapper = activityMapper;
    }

    @Override
    public PageResult<ActivityVo> page(ActivityQueryBo queryBo) {
        int page = queryBo.getPage() == null || queryBo.getPage() < 1 ? 1 : queryBo.getPage();
        int pageSize = queryBo.getPageSize() == null || queryBo.getPageSize() < 1 ? 10 : queryBo.getPageSize();
        LambdaQueryWrapper<BarActivity> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(queryBo.getTitle())) {
            wrapper.like(BarActivity::getTitle, queryBo.getTitle());
        }
        if (queryBo.getStatus() != null) {
            wrapper.eq(BarActivity::getStatus, queryBo.getStatus());
        }
        wrapper.orderByDesc(BarActivity::getCreateTime);
        Page<BarActivity> pageData = activityMapper.selectPage(new Page<>(page, pageSize), wrapper);
        List<ActivityVo> list = pageData.getRecords().stream().map(this::toVo).collect(Collectors.toList());
        return PageResult.of(list, pageData.getTotal(), page, pageSize);
    }

    @Override
    public List<ActivityVo> listEnabled(int limit) {
        return activityMapper.selectList(new LambdaQueryWrapper<BarActivity>()
                        .eq(BarActivity::getStatus, 1)
                        .orderByDesc(BarActivity::getCreateTime)
                        .last("LIMIT " + limit))
                .stream().map(this::toVo).collect(Collectors.toList());
    }

    @Override
    public ActivityVo detail(Long id) {
        BarActivity activity = getActivityOrThrow(id);
        return toVo(activity);
    }

    @Override
    public void save(ActivitySaveBo bo) {
        validateSaveBo(bo, false);
        Date now = new Date();
        BarActivity activity = new BarActivity();
        BeanUtil.copyProperties(bo, activity);
        activity.setStatus(bo.getStatus() == null ? 1 : bo.getStatus());
        activity.setCreateTime(now);
        activity.setUpdateTime(now);
        activityMapper.insert(activity);
    }

    @Override
    public void update(ActivitySaveBo bo) {
        if (bo.getId() == null) {
            throw new BusinessException("活动ID不能为空");
        }
        validateSaveBo(bo, true);
        BarActivity activity = getActivityOrThrow(bo.getId());
        activity.setTitle(bo.getTitle());
        activity.setImage(bo.getImage());
        activity.setTag(bo.getTag());
        activity.setDescription(bo.getDescription());
        activity.setPrice(bo.getPrice());
        activity.setOriginalPrice(bo.getOriginalPrice());
        activity.setStartTime(bo.getStartTime());
        activity.setEndTime(bo.getEndTime());
        if (bo.getStatus() != null) {
            activity.setStatus(bo.getStatus());
        }
        activity.setUpdateTime(new Date());
        activityMapper.updateById(activity);
    }

    @Override
    public void delete(Long id) {
        getActivityOrThrow(id);
        activityMapper.deleteById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        BarActivity activity = getActivityOrThrow(id);
        activity.setStatus(status);
        activity.setUpdateTime(new Date());
        activityMapper.updateById(activity);
    }

    private void validateSaveBo(ActivitySaveBo bo, boolean isUpdate) {
        if (!isUpdate && bo.getId() != null) {
            throw new BusinessException("新增活动不能携带ID");
        }
        if (StrUtil.isBlank(bo.getTitle())) {
            throw new BusinessException("活动标题不能为空");
        }
    }

    private BarActivity getActivityOrThrow(Long id) {
        BarActivity activity = activityMapper.selectById(id);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }
        return activity;
    }

    private ActivityVo toVo(BarActivity activity) {
        ActivityVo vo = new ActivityVo();
        BeanUtil.copyProperties(activity, vo);
        return vo;
    }
}

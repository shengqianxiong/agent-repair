package com.sqx.modules.review.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.common.PageResult;
import com.sqx.common.exception.BusinessException;
import com.sqx.modules.review.db.entity.ReviewActivity;
import com.sqx.modules.review.db.entity.ReviewRebate;
import com.sqx.modules.review.db.entity.ReviewTask;
import com.sqx.modules.review.db.mapper.ReviewActivityMapper;
import com.sqx.modules.review.db.mapper.ReviewRebateMapper;
import com.sqx.modules.review.db.mapper.ReviewTaskMapper;
import com.sqx.modules.review.domain.bo.VerifyActionBo;
import com.sqx.modules.review.domain.bo.VerifyQueryBo;
import com.sqx.modules.review.domain.vo.RebateVo;
import com.sqx.modules.review.domain.vo.TaskVo;
import com.sqx.modules.review.service.ReviewTaskService;
import com.sqx.modules.review.service.ReviewVerifyService;
import com.sqx.modules.review.util.ReviewJsonUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewVerifyServiceImpl implements ReviewVerifyService {

    private final ReviewTaskMapper taskMapper;
    private final ReviewRebateMapper rebateMapper;
    private final ReviewActivityMapper activityMapper;
    private final ReviewActivityServiceImpl activityService;
    private final ReviewTaskService taskService;

    public ReviewVerifyServiceImpl(ReviewTaskMapper taskMapper,
                                   ReviewRebateMapper rebateMapper,
                                   ReviewActivityMapper activityMapper,
                                   ReviewActivityServiceImpl activityService,
                                   ReviewTaskService taskService) {
        this.taskMapper = taskMapper;
        this.rebateMapper = rebateMapper;
        this.activityMapper = activityMapper;
        this.activityService = activityService;
        this.taskService = taskService;
    }

    @Override
    public PageResult<TaskVo> page(VerifyQueryBo queryBo) {
        int page = queryBo.getPage() == null || queryBo.getPage() < 1 ? 1 : queryBo.getPage();
        int pageSize = queryBo.getPageSize() == null || queryBo.getPageSize() < 1 ? 10 : queryBo.getPageSize();
        String status = StrUtil.blankToDefault(queryBo.getStatus(), "auditing");

        LambdaQueryWrapper<ReviewRebate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReviewRebate::getStatus, status);
        wrapper.orderByDesc(ReviewRebate::getCreateTime);
        Page<ReviewRebate> pageData = rebateMapper.selectPage(new Page<>(page, pageSize), wrapper);

        List<TaskVo> list = pageData.getRecords().stream().map(rebate -> {
            ReviewTask task = taskMapper.selectById(rebate.getTaskId());
            ReviewActivity activity = activityMapper.selectById(rebate.getActivityId());
            if (task == null) {
                return null;
            }
            if (StrUtil.isNotBlank(queryBo.getKeyword())) {
                String keyword = queryBo.getKeyword();
                boolean matched = (activity != null && (
                        StrUtil.contains(activity.getTitle(), keyword)
                                || StrUtil.contains(activity.getProductName(), keyword)))
                        || StrUtil.contains(task.getUserIdentifier(), keyword);
                if (!matched) {
                    return null;
                }
            }
            return taskService.detail(task.getId());
        }).filter(item -> item != null).collect(Collectors.toList());

        return PageResult.of(list, pageData.getTotal(), page, pageSize);
    }

    @Override
    @Transactional
    public void pass(VerifyActionBo bo) {
        ReviewRebate rebate = getRebateOrThrow(resolveVerifyId(bo));
        if (!"auditing".equals(rebate.getStatus())) {
            throw new BusinessException("当前审核单不可通过");
        }
        Date now = new Date();
        rebate.setStatus("paid");
        rebate.setPaidTime(now);
        rebate.setRejectReason(null);
        rebate.setUpdateTime(now);
        rebateMapper.updateById(rebate);

        ReviewTask task = taskMapper.selectById(rebate.getTaskId());
        if (task != null) {
            task.setStatus("paid");
            task.setUpdateTime(now);
            taskMapper.updateById(task);
        }
        activityService.incrementVerifyCount(rebate.getActivityId());
    }

    @Override
    @Transactional
    public void reject(VerifyActionBo bo) {
        ReviewRebate rebate = getRebateOrThrow(resolveVerifyId(bo));
        if (!"auditing".equals(rebate.getStatus())) {
            throw new BusinessException("当前审核单不可拒绝");
        }
        if (StrUtil.isBlank(bo.getRemark()) && StrUtil.isBlank(bo.getReason())) {
            throw new BusinessException("请填写拒绝原因");
        }
        Date now = new Date();
        rebate.setStatus("rejected");
        rebate.setRejectReason(StrUtil.blankToDefault(bo.getRemark(), bo.getReason()));
        rebate.setUpdateTime(now);
        rebateMapper.updateById(rebate);

        ReviewTask task = taskMapper.selectById(rebate.getTaskId());
        if (task != null) {
            task.setStatus("rejected");
            task.setUpdateTime(now);
            taskMapper.updateById(task);
        }
    }

    @Override
    public RebateVo rebateDetail(Long rebateId) {
        ReviewRebate rebate = getRebateOrThrow(rebateId);
        ReviewTask task = taskMapper.selectById(rebate.getTaskId());
        ReviewActivity activity = activityMapper.selectById(rebate.getActivityId());
        RebateVo vo = new RebateVo();
        vo.setRebateId(rebate.getId());
        vo.setId(rebate.getId());
        vo.setTaskId(rebate.getTaskId());
        vo.setActivityId(rebate.getActivityId());
        vo.setAmount(rebate.getAmount());
        vo.setRebateAmount(rebate.getAmount());
        vo.setStatus(rebate.getStatus());
        vo.setStatusText(ReviewJsonUtil.statusText(rebate.getStatus()));
        vo.setPaidTime(ReviewJsonUtil.formatDateTime(rebate.getPaidTime()));
        vo.setRejectReason(rebate.getRejectReason());
        if (task != null) {
            vo.setScreenshotUrl(task.getScreenshotUrl());
            vo.setReviewImage(task.getScreenshotUrl());
            vo.setTaskInfo(taskService.detail(task.getId()));
        }
        if (activity != null) {
            vo.setActivityTitle(activity.getTitle());
            vo.setProductName(activity.getProductName());
            vo.setProductImage(activity.getProductImage());
        }
        return vo;
    }

    private Long resolveVerifyId(VerifyActionBo bo) {
        if (bo.getVerifyId() != null) {
            return bo.getVerifyId();
        }
        if (bo.getId() != null) {
            return bo.getId();
        }
        throw new BusinessException("审核单ID不能为空");
    }

    private ReviewRebate getRebateOrThrow(Long rebateId) {
        ReviewRebate rebate = rebateMapper.selectById(rebateId);
        if (rebate == null) {
            throw new BusinessException("审核单不存在");
        }
        return rebate;
    }
}

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
import com.sqx.modules.review.domain.bo.TaskCopyReportBo;
import com.sqx.modules.review.domain.bo.TaskStartBo;
import com.sqx.modules.review.domain.bo.TaskSubmitBo;
import com.sqx.modules.review.domain.vo.ActivityVo;
import com.sqx.modules.review.domain.vo.TaskVo;
import com.sqx.modules.review.domain.vo.UserSummaryVo;
import com.sqx.modules.review.service.ReviewTaskService;
import com.sqx.modules.review.util.ReviewJsonUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewTaskServiceImpl implements ReviewTaskService {

    private final ReviewTaskMapper taskMapper;
    private final ReviewRebateMapper rebateMapper;
    private final ReviewActivityMapper activityMapper;
    private final ReviewActivityServiceImpl activityService;

    public ReviewTaskServiceImpl(ReviewTaskMapper taskMapper,
                                 ReviewRebateMapper rebateMapper,
                                 ReviewActivityMapper activityMapper,
                                 ReviewActivityServiceImpl activityService) {
        this.taskMapper = taskMapper;
        this.rebateMapper = rebateMapper;
        this.activityMapper = activityMapper;
        this.activityService = activityService;
    }

    @Override
    @Transactional
    public TaskVo start(TaskStartBo bo) {
        if (bo.getActivityId() == null) {
            throw new BusinessException("活动ID不能为空");
        }
        if (StrUtil.isBlank(bo.getUserIdentifier())) {
            throw new BusinessException("用户标识不能为空");
        }
        ReviewActivity activity = activityService.getActivityOrThrow(bo.getActivityId());
        if (activity.getStatus() != null && activity.getStatus() == 0) {
            throw new BusinessException("活动已下架");
        }
        Date now = new Date();
        ReviewTask task = new ReviewTask();
        task.setActivityId(activity.getId());
        task.setUserIdentifier(bo.getUserIdentifier().trim());
        task.setStatus("started");
        task.setCopyReported(0);
        task.setCreateTime(now);
        task.setUpdateTime(now);
        taskMapper.insert(task);
        activityService.incrementScanCount(activity.getId());
        return toVo(task, activity, null);
    }

    @Override
    public void reportCopy(TaskCopyReportBo bo) {
        if (bo.getTaskId() == null) {
            throw new BusinessException("任务ID不能为空");
        }
        ReviewTask task = getTaskOrThrow(bo.getTaskId());
        task.setCopyTextId(bo.getCopyTextId());
        task.setCopyReported(1);
        task.setUpdateTime(new Date());
        taskMapper.updateById(task);
    }

    @Override
    @Transactional
    public TaskVo submit(TaskSubmitBo bo) {
        if (bo.getTaskId() == null) {
            throw new BusinessException("任务ID不能为空");
        }
        if (StrUtil.isBlank(bo.getScreenshotUrl())) {
            throw new BusinessException("请上传评价截图");
        }
        if (StrUtil.isBlank(bo.getUserIdentifier())) {
            throw new BusinessException("用户标识不能为空");
        }
        ReviewTask task = getTaskOrThrow(bo.getTaskId());
        if (!task.getUserIdentifier().equals(bo.getUserIdentifier().trim())) {
            throw new BusinessException("无权操作该任务");
        }
        ReviewActivity activity = activityService.getActivityOrThrow(
                bo.getActivityId() != null ? bo.getActivityId() : task.getActivityId());
        Date now = new Date();
        task.setScreenshotUrl(bo.getScreenshotUrl());
        task.setRemark(bo.getRemark());
        task.setContact(bo.getContact());
        task.setStatus("auditing");
        task.setUpdateTime(now);
        taskMapper.updateById(task);

        ReviewRebate rebate = rebateMapper.selectOne(new LambdaQueryWrapper<ReviewRebate>()
                .eq(ReviewRebate::getTaskId, task.getId())
                .last("LIMIT 1"));
        if (rebate == null) {
            rebate = new ReviewRebate();
            rebate.setTaskId(task.getId());
            rebate.setActivityId(activity.getId());
            rebate.setUserIdentifier(task.getUserIdentifier());
            rebate.setAmount(activity.getRebateAmount());
            rebate.setStatus("auditing");
            rebate.setCreateTime(now);
            rebate.setUpdateTime(now);
            rebateMapper.insert(rebate);
            activityService.incrementSubmitCount(activity.getId());
        } else {
            rebate.setStatus("auditing");
            rebate.setRejectReason(null);
            rebate.setUpdateTime(now);
            rebateMapper.updateById(rebate);
        }
        return toVo(task, activity, rebate);
    }

    @Override
    public TaskVo detail(Long taskId) {
        ReviewTask task = getTaskOrThrow(taskId);
        ReviewActivity activity = activityService.getActivityOrThrow(task.getActivityId());
        ReviewRebate rebate = rebateMapper.selectOne(new LambdaQueryWrapper<ReviewRebate>()
                .eq(ReviewRebate::getTaskId, task.getId())
                .last("LIMIT 1"));
        return toVo(task, activity, rebate);
    }

    @Override
    public UserSummaryVo userSummary(String userIdentifier) {
        if (StrUtil.isBlank(userIdentifier)) {
            throw new BusinessException("用户标识不能为空");
        }
        List<ReviewTask> tasks = taskMapper.selectList(new LambdaQueryWrapper<ReviewTask>()
                .eq(ReviewTask::getUserIdentifier, userIdentifier.trim()));
        List<ReviewRebate> rebates = rebateMapper.selectList(new LambdaQueryWrapper<ReviewRebate>()
                .eq(ReviewRebate::getUserIdentifier, userIdentifier.trim())
                .orderByDesc(ReviewRebate::getCreateTime));

        UserSummaryVo vo = new UserSummaryVo();
        vo.setTaskCount(tasks.size());
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal paid = BigDecimal.ZERO;
        int pending = 0;
        for (ReviewRebate rebate : rebates) {
            total = total.add(rebate.getAmount() == null ? BigDecimal.ZERO : rebate.getAmount());
            if ("paid".equals(rebate.getStatus())) {
                paid = paid.add(rebate.getAmount());
            }
            if ("auditing".equals(rebate.getStatus())) {
                pending++;
            }
        }
        vo.setTotalRebate(total);
        vo.setPaidAmount(paid);
        vo.setPendingCount(pending);
        if (!rebates.isEmpty()) {
            vo.setLatestRebateId(rebates.get(0).getId());
        }
        return vo;
    }

    @Override
    public PageResult<TaskVo> userTasks(String userIdentifier, Integer page, Integer pageSize, String status) {
        if (StrUtil.isBlank(userIdentifier)) {
            throw new BusinessException("用户标识不能为空");
        }
        int current = page == null || page < 1 ? 1 : page;
        int size = pageSize == null || pageSize < 1 ? 20 : pageSize;
        LambdaQueryWrapper<ReviewTask> wrapper = new LambdaQueryWrapper<ReviewTask>()
                .eq(ReviewTask::getUserIdentifier, userIdentifier.trim());
        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(ReviewTask::getStatus, status.trim());
        }
        wrapper.orderByDesc(ReviewTask::getCreateTime);
        Page<ReviewTask> pageData = taskMapper.selectPage(new Page<>(current, size), wrapper);
        List<TaskVo> list = pageData.getRecords().stream().map(task -> {
            ReviewActivity activity = activityMapper.selectById(task.getActivityId());
            ReviewRebate rebate = rebateMapper.selectOne(new LambdaQueryWrapper<ReviewRebate>()
                    .eq(ReviewRebate::getTaskId, task.getId())
                    .last("LIMIT 1"));
            return toVo(task, activity, rebate);
        }).collect(Collectors.toList());
        return PageResult.of(list, pageData.getTotal(), current, size);
    }

    private ReviewTask getTaskOrThrow(Long taskId) {
        ReviewTask task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        return task;
    }

    private TaskVo toVo(ReviewTask task, ReviewActivity activity, ReviewRebate rebate) {
        TaskVo vo = new TaskVo();
        vo.setTaskId(task.getId());
        vo.setId(task.getId());
        vo.setActivityId(task.getActivityId());
        vo.setUserIdentifier(task.getUserIdentifier());
        vo.setStatus(task.getStatus());
        ReviewJsonUtil.fillTaskStatus(vo);
        vo.setScreenshotUrl(task.getScreenshotUrl());
        vo.setRemark(task.getRemark());
        vo.setContact(task.getContact());
        vo.setSubmitTime(ReviewJsonUtil.formatDateTime(task.getUpdateTime()));
        if (activity != null) {
            vo.setActivityTitle(activity.getTitle());
            vo.setProductName(activity.getProductName());
            vo.setProductImage(activity.getProductImage());
            vo.setRebateAmount(activity.getRebateAmount());
            ActivityVo activityVo = activityService.toVo(activity);
            vo.setActivityInfo(activityVo);
        }
        if (rebate != null) {
            vo.setRebateId(rebate.getId());
            if ("paid".equals(rebate.getStatus())) {
                vo.setPaidTime(ReviewJsonUtil.formatDateTime(rebate.getPaidTime()));
            }
            vo.setRejectReason(rebate.getRejectReason());
            vo.setStatus(rebate.getStatus());
            ReviewJsonUtil.fillTaskStatus(vo);
        }
        return vo;
    }
}

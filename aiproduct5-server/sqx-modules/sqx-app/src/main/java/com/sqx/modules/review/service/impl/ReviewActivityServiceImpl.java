package com.sqx.modules.review.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.common.PageResult;
import com.sqx.common.exception.BusinessException;
import com.sqx.modules.review.db.entity.ReviewActivity;
import com.sqx.modules.review.db.mapper.ReviewActivityMapper;
import com.sqx.modules.review.db.mapper.ReviewRebateMapper;
import com.sqx.modules.review.db.mapper.ReviewTaskMapper;
import com.sqx.modules.review.domain.bo.ActivityQueryBo;
import com.sqx.modules.review.domain.bo.ActivitySaveBo;
import com.sqx.modules.review.domain.bo.CopywritingItemBo;
import com.sqx.modules.review.domain.vo.ActivityVo;
import com.sqx.modules.review.domain.vo.AiHistoryVo;
import com.sqx.modules.review.domain.vo.CopywritingVo;
import com.sqx.modules.review.domain.vo.DashboardVo;
import com.sqx.modules.review.service.ReviewActivityService;
import com.sqx.modules.review.util.ReviewJsonUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewActivityServiceImpl implements ReviewActivityService {

    private final ReviewActivityMapper activityMapper;
    private final ReviewTaskMapper taskMapper;
    private final ReviewRebateMapper rebateMapper;

    public ReviewActivityServiceImpl(ReviewActivityMapper activityMapper,
                                     ReviewTaskMapper taskMapper,
                                     ReviewRebateMapper rebateMapper) {
        this.activityMapper = activityMapper;
        this.taskMapper = taskMapper;
        this.rebateMapper = rebateMapper;
    }

    @Override
    public ActivityVo getByCode(String code) {
        if (StrUtil.isBlank(code)) {
            throw new BusinessException("活动编码不能为空");
        }
        ReviewActivity activity = activityMapper.selectOne(new LambdaQueryWrapper<ReviewActivity>()
                .eq(ReviewActivity::getCode, code.trim()));
        if (activity == null) {
            throw new BusinessException("活动不存在或已失效");
        }
        if (activity.getStatus() != null && activity.getStatus() == 0) {
            throw new BusinessException("活动已下架");
        }
        if (activity.getValidEndTime() != null && activity.getValidEndTime().before(new Date())) {
            throw new BusinessException("活动已过期");
        }
        return toVo(activity);
    }

    @Override
    public ActivityVo create(ActivitySaveBo bo) {
        validateSaveBo(bo);
        Date now = new Date();
        ReviewActivity activity = new ReviewActivity();
        fillActivityFromBo(activity, bo);
        activity.setCode(generateCode());
        activity.setStatus(1);
        activity.setScanCount(0);
        activity.setSubmitCount(0);
        activity.setVerifyCount(0);
        activity.setCreateTime(now);
        activity.setUpdateTime(now);
        activityMapper.insert(activity);
        ActivityVo vo = toVo(activity);
        vo.setQrcodeUrl("/pages/task/index?code=" + activity.getCode());
        return vo;
    }

    @Override
    public ActivityVo update(ActivitySaveBo bo) {
        if (bo.getId() == null) {
            throw new BusinessException("活动ID不能为空");
        }
        ReviewActivity activity = getActivityOrThrow(bo.getId());
        validateSaveBo(bo);
        fillActivityFromBo(activity, bo);
        activity.setUpdateTime(new Date());
        activityMapper.updateById(activity);
        return toVo(activity);
    }

    @Override
    public PageResult<ActivityVo> page(ActivityQueryBo queryBo) {
        int page = queryBo.getPage() == null || queryBo.getPage() < 1 ? 1 : queryBo.getPage();
        int pageSize = queryBo.getPageSize() == null || queryBo.getPageSize() < 1 ? 10 : queryBo.getPageSize();

        LambdaQueryWrapper<ReviewActivity> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(queryBo.getKeyword())) {
            wrapper.and(w -> w.like(ReviewActivity::getTitle, queryBo.getKeyword())
                    .or().like(ReviewActivity::getProductName, queryBo.getKeyword())
                    .or().like(ReviewActivity::getCode, queryBo.getKeyword()));
        }
        if (queryBo.getStatus() != null) {
            wrapper.eq(ReviewActivity::getStatus, queryBo.getStatus());
        }
        wrapper.orderByDesc(ReviewActivity::getCreateTime);

        Page<ReviewActivity> pageData = activityMapper.selectPage(new Page<>(page, pageSize), wrapper);
        List<ActivityVo> list = pageData.getRecords().stream().map(this::toVo).collect(Collectors.toList());
        return PageResult.of(list, pageData.getTotal(), page, pageSize);
    }

    @Override
    public ActivityVo detail(Long id) {
        return toVo(getActivityOrThrow(id));
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        ReviewActivity activity = getActivityOrThrow(id);
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException("活动状态无效");
        }
        activity.setStatus(status);
        activity.setUpdateTime(new Date());
        activityMapper.updateById(activity);
    }

    @Override
    public DashboardVo dashboard() {
        List<ReviewActivity> activities = activityMapper.selectList(null);
        DashboardVo vo = new DashboardVo();
        vo.setActivityCount(activities.size());
        int scan = 0;
        int submit = 0;
        int verify = 0;
        BigDecimal expense = BigDecimal.ZERO;
        for (ReviewActivity activity : activities) {
            scan += activity.getScanCount() == null ? 0 : activity.getScanCount();
            submit += activity.getSubmitCount() == null ? 0 : activity.getSubmitCount();
            verify += activity.getVerifyCount() == null ? 0 : activity.getVerifyCount();
        }
        vo.setScanCount(scan);
        vo.setSubmitCount(submit);
        vo.setVerifyCount(verify);
        vo.setRebateExpense(expense);
        return vo;
    }

    @Override
    public PageResult<AiHistoryVo> aiHistory(Long activityId, Integer page, Integer pageSize) {
        ActivityQueryBo queryBo = new ActivityQueryBo();
        queryBo.setPage(page);
        queryBo.setPageSize(pageSize);
        PageResult<ActivityVo> activityPage = page(queryBo);
        List<AiHistoryVo> list = new ArrayList<>();
        for (ActivityVo activity : activityPage.getList()) {
            if (activityId != null && !activityId.equals(activity.getId())) {
                continue;
            }
            AiHistoryVo item = new AiHistoryVo();
            item.setActivityId(activity.getId());
            item.setActivityTitle(activity.getTitle());
            item.setStatus(activity.getStatus() != null && activity.getStatus() == 1 ? "success" : "disabled");
            item.setCopywritingList(activity.getCopywritingList());
            item.setRecommendImages(activity.getRecommendImages());
            list.add(item);
        }
        return PageResult.of(list, activityPage.getTotal(), activityPage.getPage(), activityPage.getPageSize());
    }

    public void incrementScanCount(Long activityId) {
        ReviewActivity activity = getActivityOrThrow(activityId);
        activity.setScanCount((activity.getScanCount() == null ? 0 : activity.getScanCount()) + 1);
        activity.setUpdateTime(new Date());
        activityMapper.updateById(activity);
    }

    public void incrementSubmitCount(Long activityId) {
        ReviewActivity activity = getActivityOrThrow(activityId);
        activity.setSubmitCount((activity.getSubmitCount() == null ? 0 : activity.getSubmitCount()) + 1);
        activity.setUpdateTime(new Date());
        activityMapper.updateById(activity);
    }

    public void incrementVerifyCount(Long activityId) {
        ReviewActivity activity = getActivityOrThrow(activityId);
        activity.setVerifyCount((activity.getVerifyCount() == null ? 0 : activity.getVerifyCount()) + 1);
        activity.setUpdateTime(new Date());
        activityMapper.updateById(activity);
    }

    ReviewActivity getActivityOrThrow(Long id) {
        ReviewActivity activity = activityMapper.selectById(id);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }
        return activity;
    }

    private void validateSaveBo(ActivitySaveBo bo) {
        if (StrUtil.isBlank(bo.getProductName()) && StrUtil.isBlank(bo.getTitle())) {
            throw new BusinessException("商品名称或活动标题不能为空");
        }
        BigDecimal amount = bo.getRebateAmount() != null ? bo.getRebateAmount() : bo.getRewardAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("返利金额必须大于0");
        }
    }

    private void fillActivityFromBo(ReviewActivity activity, ActivitySaveBo bo) {
        activity.setTitle(StrUtil.blankToDefault(bo.getTitle(), bo.getProductName()));
        activity.setProductName(StrUtil.blankToDefault(bo.getProductName(), bo.getTitle()));
        activity.setProductUrl(bo.getProductUrl());
        activity.setShopName(StrUtil.blankToDefault(bo.getShopName(), "豆评严选旗舰店"));
        activity.setProductImage(StrUtil.blankToDefault(bo.getProductImage(), bo.getImageUrl()));
        activity.setDouyinLink(StrUtil.blankToDefault(bo.getDouyinLink(), "https://www.douyin.com"));
        BigDecimal amount = bo.getRebateAmount() != null ? bo.getRebateAmount() : bo.getRewardAmount();
        activity.setRebateAmount(amount);
        String expire = StrUtil.blankToDefault(bo.getValidEndTime(), bo.getExpireTime());
        activity.setValidEndTime(ReviewJsonUtil.parseDateTime(expire));
        List<CopywritingVo> copywritingList = resolveCopywriting(bo);
        List<String> images = resolveImages(bo);
        activity.setCopywritingJson(ReviewJsonUtil.toCopywritingJson(copywritingList));
        activity.setRecommendImagesJson(ReviewJsonUtil.toImagesJson(images));
        if (bo.getStatus() != null) {
            activity.setStatus(bo.getStatus());
        }
    }

    private List<CopywritingVo> resolveCopywriting(ActivitySaveBo bo) {
        if (bo.getCopywritingList() != null && !bo.getCopywritingList().isEmpty()) {
            List<CopywritingVo> list = new ArrayList<>();
            long index = 1;
            for (CopywritingItemBo item : bo.getCopywritingList()) {
                CopywritingVo vo = new CopywritingVo();
                vo.setId(item.getId() == null ? index : item.getId());
                vo.setTitle(StrUtil.blankToDefault(item.getTitle(), "推荐文案" + index));
                vo.setContent(item.getContent());
                list.add(vo);
                index++;
            }
            return list;
        }
        if (StrUtil.isNotBlank(bo.getCopywriting())) {
            CopywritingVo vo = new CopywritingVo();
            vo.setId(1L);
            vo.setTitle("商家文案");
            vo.setContent(bo.getCopywriting());
            List<CopywritingVo> list = new ArrayList<>();
            list.add(vo);
            return list;
        }
        return ReviewJsonUtil.defaultCopywriting(StrUtil.blankToDefault(bo.getProductName(), "商品"));
    }

    private List<String> resolveImages(ActivitySaveBo bo) {
        if (bo.getRecommendImages() != null && !bo.getRecommendImages().isEmpty()) {
            return bo.getRecommendImages();
        }
        if (StrUtil.isNotBlank(bo.getImageUrl())) {
            List<String> images = new ArrayList<>();
            images.add(bo.getImageUrl());
            return images;
        }
        if (StrUtil.isNotBlank(bo.getProductImage())) {
            List<String> images = new ArrayList<>();
            images.add(bo.getProductImage());
            return images;
        }
        return new ArrayList<>();
    }

    private String generateCode() {
        return "DP" + System.currentTimeMillis();
    }

    ActivityVo toVo(ReviewActivity activity) {
        ActivityVo vo = new ActivityVo();
        vo.setId(activity.getId());
        vo.setCode(activity.getCode());
        vo.setTitle(activity.getTitle());
        vo.setProductName(activity.getProductName());
        vo.setProductImage(activity.getProductImage());
        vo.setShopName(activity.getShopName());
        vo.setProductUrl(activity.getProductUrl());
        vo.setRebateAmount(activity.getRebateAmount());
        vo.setValidEndTime(ReviewJsonUtil.formatDateTime(activity.getValidEndTime()));
        vo.setDouyinLink(activity.getDouyinLink());
        vo.setCopywritingList(ReviewJsonUtil.parseCopywriting(activity.getCopywritingJson()));
        vo.setRecommendImages(ReviewJsonUtil.parseImages(activity.getRecommendImagesJson()));
        vo.setStatus(activity.getStatus());
        vo.setScanCount(activity.getScanCount());
        vo.setSubmitCount(activity.getSubmitCount());
        vo.setVerifyCount(activity.getVerifyCount());
        vo.setQrcodeUrl("/pages/task/index?code=" + activity.getCode());
        return vo;
    }
}

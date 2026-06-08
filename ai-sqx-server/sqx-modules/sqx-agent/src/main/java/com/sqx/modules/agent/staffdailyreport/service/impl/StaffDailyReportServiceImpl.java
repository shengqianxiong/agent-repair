package com.sqx.modules.agent.staffdailyreport.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.common.PageResult;
import com.sqx.common.exception.BusinessException;
import com.sqx.modules.agent.base.AbstractAgentService;
import com.sqx.modules.agent.base.AgentPageQueryBo;
import com.sqx.modules.agent.base.RecycleBinSupport;
import com.sqx.modules.agent.staffdailyreport.db.entity.StaffDailyReport;
import com.sqx.modules.agent.staffdailyreport.db.mapper.StaffDailyReportMapper;
import com.sqx.modules.agent.staffdailyreport.domain.bo.StaffDailyReportQueryBo;
import com.sqx.modules.agent.staffdailyreport.domain.bo.StaffDailyReportSaveBo;
import com.sqx.modules.agent.staffdailyreport.service.StaffDailyReportService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class StaffDailyReportServiceImpl extends AbstractAgentService<StaffDailyReport, StaffDailyReportMapper>
        implements StaffDailyReportService {

    public StaffDailyReportServiceImpl(StaffDailyReportMapper mapper, RecycleBinSupport recycleBinSupport) {
        super(mapper, recycleBinSupport);
    }

    @Override
    protected String tableName() {
        return "staff_daily_report";
    }

    @Override
    protected Class<StaffDailyReport> entityClass() {
        return StaffDailyReport.class;
    }

    @Override
    protected String entityLabel() {
        return "员工日报";
    }

    @Override
    protected com.baomidou.mybatisplus.core.toolkit.support.SFunction<StaffDailyReport, Date> createTimeColumn() {
        return StaffDailyReport::getCreateTime;
    }

    @Override
    public PageResult<StaffDailyReport> page(StaffDailyReportQueryBo queryBo) {
        LambdaQueryWrapper<StaffDailyReport> wrapper = new LambdaQueryWrapper<>();
        if (queryBo.getEmployeeId() != null) {
            wrapper.eq(StaffDailyReport::getEmployeeId, queryBo.getEmployeeId());
        }
        if (StrUtil.isNotBlank(queryBo.getKeyword())) {
            wrapper.like(StaffDailyReport::getContent, queryBo.getKeyword());
        }
        if (queryBo.getStatus() != null) {
            wrapper.eq(StaffDailyReport::getStatus, queryBo.getStatus());
        }
        return pageQuery(queryBo, wrapper);
    }

    @Override
    public StaffDailyReport detail(Long id) {
        return getOrThrow(id);
    }

    @Override
    public void save(StaffDailyReportSaveBo bo) {
        validateSaveBo(bo, false);
        StaffDailyReport entity = new StaffDailyReport();
        entity.setEmployeeId(bo.getEmployeeId());
        entity.setReportDate(bo.getReportDate() == null ? new Date() : bo.getReportDate());
        entity.setContent(bo.getContent());
        entity.setStatus(bo.getStatus() == null ? 0 : bo.getStatus());
        fillCreateTime(entity);
        mapper.insert(entity);
    }

    @Override
    public void update(StaffDailyReportSaveBo bo) {
        if (bo.getId() == null) {
            throw new BusinessException("员工日报ID不能为空");
        }
        validateSaveBo(bo, true);
        StaffDailyReport entity = getOrThrow(bo.getId());
        if (bo.getEmployeeId() != null) {
            entity.setEmployeeId(bo.getEmployeeId());
        }
        if (bo.getReportDate() != null) {
            entity.setReportDate(bo.getReportDate());
        }
        entity.setContent(bo.getContent());
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

    private void validateSaveBo(StaffDailyReportSaveBo bo, boolean isUpdate) {
        if (!isUpdate && bo.getId() != null) {
            throw new BusinessException("新增员工日报不能携带ID");
        }
        if (bo.getEmployeeId() == null) {
            throw new BusinessException("员工ID不能为空");
        }
        if (StrUtil.isBlank(bo.getContent())) {
            throw new BusinessException("日报内容不能为空");
        }
    }
}

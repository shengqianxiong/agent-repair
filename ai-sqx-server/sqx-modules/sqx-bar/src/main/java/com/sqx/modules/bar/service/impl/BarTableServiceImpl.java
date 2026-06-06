package com.sqx.modules.bar.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.common.exception.BusinessException;
import com.sqx.modules.bar.constant.BarConstants;
import com.sqx.modules.bar.db.entity.BarTable;
import com.sqx.modules.bar.db.mapper.BarTableMapper;
import com.sqx.modules.bar.domain.bo.TableSaveBo;
import com.sqx.modules.bar.domain.bo.TableStatusBo;
import com.sqx.modules.bar.domain.vo.TableVo;
import com.sqx.modules.bar.service.BarTableService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BarTableServiceImpl implements BarTableService {

    private final BarTableMapper tableMapper;

    public BarTableServiceImpl(BarTableMapper tableMapper) {
        this.tableMapper = tableMapper;
    }

    @Override
    public List<TableVo> listAll() {
        return tableMapper.selectList(new LambdaQueryWrapper<BarTable>()
                        .orderByAsc(BarTable::getTableNo))
                .stream().map(this::toVo).collect(Collectors.toList());
    }

    @Override
    public List<TableVo> listAvailable() {
        return tableMapper.selectList(new LambdaQueryWrapper<BarTable>()
                        .eq(BarTable::getStatus, BarConstants.TABLE_STATUS_FREE)
                        .orderByAsc(BarTable::getTableNo))
                .stream().map(this::toVo).collect(Collectors.toList());
    }

    @Override
    public void save(TableSaveBo bo) {
        validateSaveBo(bo, false);
        Date now = new Date();
        BarTable table = new BarTable();
        BeanUtil.copyProperties(bo, table);
        table.setStatus(StrUtil.blankToDefault(bo.getStatus(), BarConstants.TABLE_STATUS_FREE));
        table.setCapacity(bo.getCapacity() == null ? 4 : bo.getCapacity());
        table.setCreateTime(now);
        table.setUpdateTime(now);
        tableMapper.insert(table);
    }

    @Override
    public void update(TableSaveBo bo) {
        if (bo.getId() == null) {
            throw new BusinessException("桌位ID不能为空");
        }
        validateSaveBo(bo, true);
        BarTable table = getTableOrThrow(bo.getId());
        table.setTableNo(bo.getTableNo());
        table.setArea(bo.getArea());
        if (bo.getCapacity() != null) {
            table.setCapacity(bo.getCapacity());
        }
        if (StrUtil.isNotBlank(bo.getStatus())) {
            table.setStatus(bo.getStatus());
        }
        table.setUpdateTime(new Date());
        tableMapper.updateById(table);
    }

    @Override
    public void updateStatus(TableStatusBo bo) {
        if (bo.getId() == null || StrUtil.isBlank(bo.getStatus())) {
            throw new BusinessException("参数不完整");
        }
        BarTable table = getTableOrThrow(bo.getId());
        table.setStatus(bo.getStatus());
        table.setUpdateTime(new Date());
        tableMapper.updateById(table);
    }

    private void validateSaveBo(TableSaveBo bo, boolean isUpdate) {
        if (!isUpdate && bo.getId() != null) {
            throw new BusinessException("新增桌位不能携带ID");
        }
        if (StrUtil.isBlank(bo.getTableNo())) {
            throw new BusinessException("桌号不能为空");
        }
    }

    private BarTable getTableOrThrow(Long id) {
        BarTable table = tableMapper.selectById(id);
        if (table == null) {
            throw new BusinessException("桌位不存在");
        }
        return table;
    }

    private TableVo toVo(BarTable table) {
        TableVo vo = new TableVo();
        BeanUtil.copyProperties(table, vo);
        return vo;
    }
}

package com.sqx.modules.bar.service;

import com.sqx.modules.bar.domain.bo.TableSaveBo;
import com.sqx.modules.bar.domain.bo.TableStatusBo;
import com.sqx.modules.bar.domain.vo.TableVo;

import java.util.List;

public interface BarTableService {

    List<TableVo> listAll();

    List<TableVo> listAvailable();

    void save(TableSaveBo bo);

    void update(TableSaveBo bo);

    void updateStatus(TableStatusBo bo);
}

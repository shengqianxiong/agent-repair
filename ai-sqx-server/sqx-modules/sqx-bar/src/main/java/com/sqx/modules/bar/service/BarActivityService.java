package com.sqx.modules.bar.service;

import com.sqx.common.PageResult;
import com.sqx.modules.bar.domain.bo.ActivityQueryBo;
import com.sqx.modules.bar.domain.bo.ActivitySaveBo;
import com.sqx.modules.bar.domain.vo.ActivityVo;

import java.util.List;

public interface BarActivityService {

    PageResult<ActivityVo> page(ActivityQueryBo queryBo);

    List<ActivityVo> listEnabled(int limit);

    ActivityVo detail(Long id);

    void save(ActivitySaveBo bo);

    void update(ActivitySaveBo bo);

    void delete(Long id);

    void updateStatus(Long id, Integer status);
}

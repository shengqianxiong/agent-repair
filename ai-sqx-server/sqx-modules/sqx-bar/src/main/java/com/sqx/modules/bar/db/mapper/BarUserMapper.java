package com.sqx.modules.bar.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sqx.modules.bar.db.entity.BarUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BarUserMapper extends BaseMapper<BarUser> {
}

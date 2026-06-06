package com.sqx.modules.auth.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sqx.modules.auth.db.entity.AppUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * App 端用户 Mapper。
 */
@Mapper
public interface AppUserMapper extends BaseMapper<AppUser> {
}

package com.sqx.modules.auth.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sqx.modules.auth.db.entity.AuthAppUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户端用户 Mapper
 */
@Mapper
public interface AuthAppUserMapper extends BaseMapper<AuthAppUser> {
}

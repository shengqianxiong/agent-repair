package com.sqx.modules.auth.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sqx.modules.auth.db.entity.AuthAdminUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理端用户 Mapper
 */
@Mapper
public interface AuthAdminUserMapper extends BaseMapper<AuthAdminUser> {
}

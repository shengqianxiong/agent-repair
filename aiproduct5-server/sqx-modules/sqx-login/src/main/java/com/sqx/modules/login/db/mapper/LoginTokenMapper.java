package com.sqx.modules.login.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sqx.modules.login.db.entity.LoginToken;
import org.apache.ibatis.annotations.Mapper;

/**
 * 登录 Token Mapper
 */
@Mapper
public interface LoginTokenMapper extends BaseMapper<LoginToken> {
}

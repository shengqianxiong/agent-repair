package com.sqx.modules.login.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sqx.modules.login.db.entity.LoginAccount;
import org.apache.ibatis.annotations.Mapper;

/**
 * 登录账号 Mapper
 */
@Mapper
public interface LoginAccountMapper extends BaseMapper<LoginAccount> {
}

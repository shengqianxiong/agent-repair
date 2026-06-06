package com.sqx.modules.auth.service;

import com.sqx.modules.auth.domain.bo.LoginBo;
import com.sqx.modules.auth.domain.vo.LoginVo;

/**
 * App 端用户服务。
 */
public interface AppUserService {

    /**
     * 用户登录。
     *
     * @param loginBo 登录参数
     * @return 登录结果
     */
    LoginVo login(LoginBo loginBo);
}

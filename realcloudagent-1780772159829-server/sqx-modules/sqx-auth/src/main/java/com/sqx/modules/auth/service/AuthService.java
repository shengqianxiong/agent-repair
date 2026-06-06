package com.sqx.modules.auth.service;

import com.sqx.modules.auth.domain.bo.LoginBo;
import com.sqx.modules.auth.domain.vo.LoginVo;

/**
 * 登录认证服务
 */
public interface AuthService {

    /**
     * 管理端用户登录
     *
     * @param loginBo 登录参数
     * @return 登录结果
     */
    LoginVo adminLogin(LoginBo loginBo);

    /**
     * 用户端用户登录
     *
     * @param loginBo 登录参数
     * @return 登录结果
     */
    LoginVo appLogin(LoginBo loginBo);
}

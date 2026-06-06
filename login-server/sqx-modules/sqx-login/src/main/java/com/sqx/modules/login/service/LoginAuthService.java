package com.sqx.modules.login.service;

import com.sqx.modules.login.domain.bo.LoginBo;
import com.sqx.modules.login.domain.vo.LoginVo;

/**
 * 登录认证服务
 */
public interface LoginAuthService {

    /**
     * 用户端登录
     *
     * @param loginBo 登录参数
     * @return 登录结果
     */
    LoginVo appLogin(LoginBo loginBo);

    /**
     * 管理端登录（仅管理员可登录）
     *
     * @param loginBo 登录参数
     * @return 登录结果
     */
    LoginVo adminLogin(LoginBo loginBo);
}

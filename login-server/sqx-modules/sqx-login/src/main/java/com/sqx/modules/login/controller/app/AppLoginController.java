package com.sqx.modules.login.controller.app;

import com.sqx.common.Result;
import com.sqx.modules.login.domain.bo.LoginBo;
import com.sqx.modules.login.domain.vo.LoginVo;
import com.sqx.modules.login.service.LoginAuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户端 - 账号登录
 */
@RestController
@RequestMapping("/app/login")
public class AppLoginController {

    private final LoginAuthService loginAuthService;

    public AppLoginController(LoginAuthService loginAuthService) {
        this.loginAuthService = loginAuthService;
    }

    /**
     * 用户登录，成功后返回会话 Token（有效期 2 小时）
     */
    @PostMapping
    public Result<LoginVo> login(@RequestBody LoginBo loginBo) {
        return Result.ok(loginAuthService.appLogin(loginBo));
    }
}

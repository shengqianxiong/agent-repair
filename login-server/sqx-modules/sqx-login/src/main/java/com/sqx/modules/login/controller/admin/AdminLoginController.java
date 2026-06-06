package com.sqx.modules.login.controller.admin;

import com.sqx.common.Result;
import com.sqx.modules.login.domain.bo.LoginBo;
import com.sqx.modules.login.domain.vo.LoginVo;
import com.sqx.modules.login.service.LoginAuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端 - 管理员登录
 */
@RestController
@RequestMapping("/admin/login")
public class AdminLoginController {

    private final LoginAuthService loginAuthService;

    public AdminLoginController(LoginAuthService loginAuthService) {
        this.loginAuthService = loginAuthService;
    }

    /**
     * 管理员登录，成功后返回会话 Token（有效期 2 小时）
     */
    @PostMapping
    public Result<LoginVo> login(@RequestBody LoginBo loginBo) {
        return Result.ok(loginAuthService.adminLogin(loginBo));
    }
}

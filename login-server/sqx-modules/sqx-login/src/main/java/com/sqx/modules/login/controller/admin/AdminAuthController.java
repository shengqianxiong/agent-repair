package com.sqx.modules.login.controller.admin;

import com.sqx.common.Result;
import com.sqx.modules.login.domain.bo.AccountLoginBo;
import com.sqx.modules.login.domain.vo.LoginVo;
import com.sqx.modules.login.service.LoginAccountService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端 - 管理员登录
 */
@RestController
@RequestMapping("/admin/auth")
public class AdminAuthController {

    private final LoginAccountService accountService;

    public AdminAuthController(LoginAccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public Result<LoginVo> login(@RequestBody AccountLoginBo loginBo) {
        return Result.ok(accountService.adminLogin(loginBo));
    }
}

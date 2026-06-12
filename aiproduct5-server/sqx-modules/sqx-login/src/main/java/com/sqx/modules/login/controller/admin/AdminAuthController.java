package com.sqx.modules.login.controller.admin;

import com.sqx.common.Result;
import com.sqx.modules.login.domain.bo.LoginBo;
import com.sqx.modules.login.domain.vo.LoginVo;
import com.sqx.modules.login.service.AccountService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端 - 认证接口
 */
@RestController
@RequestMapping("/admin/auth")
public class AdminAuthController {

    private final AccountService accountService;

    public AdminAuthController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public Result<LoginVo> login(@RequestBody LoginBo loginBo) {
        return Result.ok(accountService.login(loginBo, true));
    }
}

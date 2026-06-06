package com.sqx.modules.login.controller.app;

import com.sqx.common.Result;
import com.sqx.common.annotation.Login;
import com.sqx.common.utils.UserContext;
import com.sqx.modules.login.domain.bo.AccountLoginBo;
import com.sqx.modules.login.domain.vo.AccountVo;
import com.sqx.modules.login.domain.vo.LoginVo;
import com.sqx.modules.login.service.LoginAccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户端 - 登录
 */
@RestController
@RequestMapping("/app/user")
public class AppUserController {

    private final LoginAccountService accountService;

    public AppUserController(LoginAccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginVo> login(@RequestBody AccountLoginBo loginBo) {
        return Result.ok(accountService.appLogin(loginBo));
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/info")
    @Login
    public Result<AccountVo> info() {
        return Result.ok(accountService.currentAccount(UserContext.getUserId()));
    }
}

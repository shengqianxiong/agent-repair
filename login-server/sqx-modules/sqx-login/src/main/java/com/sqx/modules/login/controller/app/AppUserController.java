package com.sqx.modules.login.controller.app;

import com.sqx.common.Result;
import com.sqx.modules.login.domain.bo.LoginBo;
import com.sqx.modules.login.domain.vo.LoginVo;
import com.sqx.modules.login.service.AccountService;
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

    private final AccountService accountService;

    public AppUserController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginVo> login(@RequestBody LoginBo loginBo) {
        return Result.ok(accountService.appLogin(loginBo));
    }
}

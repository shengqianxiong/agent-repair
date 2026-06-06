package com.sqx.modules.login.controller.app;

import com.sqx.common.PageResult;
import com.sqx.common.Result;
import com.sqx.common.annotation.Login;
import com.sqx.common.utils.UserContext;
import com.sqx.modules.login.domain.vo.AccountVo;
import com.sqx.modules.login.service.LoginAccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

/**
 * 用户端 - 当前账号信息
 */
@Login
@RestController
@RequestMapping("/app/account")
public class AppAccountController {

    private final LoginAccountService accountService;

    public AppAccountController(LoginAccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * 获取当前登录账号列表（仅包含本人）
     */
    @GetMapping("/list")
    public Result<PageResult<AccountVo>> list() {
        AccountVo accountVo = accountService.currentDetail(UserContext.getUserId());
        return Result.ok(PageResult.of(Collections.singletonList(accountVo), 1, 1, 1));
    }

    /**
     * 获取当前登录账号详情
     */
    @GetMapping("/detail")
    public Result<AccountVo> detail() {
        return Result.ok(accountService.currentDetail(UserContext.getUserId()));
    }
}

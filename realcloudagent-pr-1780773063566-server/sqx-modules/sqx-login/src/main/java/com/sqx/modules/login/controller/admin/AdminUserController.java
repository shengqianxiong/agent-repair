package com.sqx.modules.login.controller.admin;

import com.sqx.common.Result;
import com.sqx.common.annotation.AdminLogin;
import com.sqx.common.utils.UserContext;
import com.sqx.modules.login.domain.bo.LoginBo;
import com.sqx.modules.login.domain.vo.LoginVo;
import com.sqx.modules.login.domain.vo.UserInfoVo;
import com.sqx.modules.login.service.AccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端 - 用户认证接口
 */
@RestController
@RequestMapping("/admin/user")
public class AdminUserController {

    private final AccountService accountService;

    public AdminUserController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public Result<LoginVo> login(@RequestBody LoginBo loginBo) {
        return Result.ok(accountService.login(loginBo, true));
    }

    /**
     * 获取当前管理员信息
     */
    @AdminLogin
    @GetMapping("/info")
    public Result<UserInfoVo> info() {
        return Result.ok(accountService.currentUserInfo(UserContext.getUserId()));
    }
}

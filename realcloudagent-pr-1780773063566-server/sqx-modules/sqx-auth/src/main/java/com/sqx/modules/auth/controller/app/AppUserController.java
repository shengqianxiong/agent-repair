package com.sqx.modules.auth.controller.app;

import com.sqx.common.Result;
import com.sqx.modules.auth.domain.bo.LoginBo;
import com.sqx.modules.auth.domain.vo.LoginVo;
import com.sqx.modules.auth.service.AppUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * App 端用户接口，提供用户登录能力。
 */
@RestController
@RequestMapping("/app/user")
public class AppUserController {

    private final AppUserService appUserService;

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    /**
     * 用户登录。
     *
     * @param loginBo 用户名与密码
     * @return 登录令牌及用户信息
     */
    @PostMapping("/login")
    public Result<LoginVo> login(@RequestBody LoginBo loginBo) {
        return Result.ok(appUserService.login(loginBo));
    }
}

package com.sqx.modules.auth.controller.app;

import com.sqx.common.Result;
import com.sqx.modules.auth.domain.bo.LoginBo;
import com.sqx.modules.auth.domain.vo.LoginVo;
import com.sqx.modules.auth.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户端认证接口
 */
@RestController
@RequestMapping("/app/auth")
public class AppAuthController {

    private final AuthService authService;

    public AppAuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户端登录
     *
     * @param loginBo 用户名与密码
     * @return Token 与用户名
     */
    @PostMapping("/login")
    public Result<LoginVo> login(@RequestBody LoginBo loginBo) {
        return Result.ok(authService.appLogin(loginBo));
    }
}

package com.sqx.modules.auth.controller.admin;

import com.sqx.common.Result;
import com.sqx.modules.auth.domain.bo.LoginBo;
import com.sqx.modules.auth.domain.vo.LoginVo;
import com.sqx.modules.auth.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端认证接口
 */
@RestController
@RequestMapping("/admin/auth")
public class AdminAuthController {

    private final AuthService authService;

    public AdminAuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 管理端登录
     *
     * @param loginBo 用户名与密码
     * @return Token 与用户名
     */
    @PostMapping("/login")
    public Result<LoginVo> login(@RequestBody LoginBo loginBo) {
        return Result.ok(authService.adminLogin(loginBo));
    }
}

package com.sqx.modules.auth.controller.admin;

import com.sqx.common.Result;
import com.sqx.modules.auth.domain.bo.LoginBo;
import com.sqx.modules.auth.domain.vo.LoginVo;
import com.sqx.modules.auth.service.AdminUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端登录接口，提供管理员登录能力。
 */
@RestController
@RequestMapping("/admin")
public class AdminLoginController {

    private final AdminUserService adminUserService;

    public AdminLoginController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    /**
     * 管理员登录。
     *
     * @param loginBo 用户名与密码
     * @return 登录令牌及管理员信息
     */
    @PostMapping("/login")
    public Result<LoginVo> login(@RequestBody LoginBo loginBo) {
        return Result.ok(adminUserService.login(loginBo));
    }
}

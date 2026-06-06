package com.sqx.modules.bar.controller.app;

import com.sqx.common.Result;
import com.sqx.common.annotation.Login;
import com.sqx.common.utils.UserContext;
import com.sqx.modules.bar.domain.bo.UserLoginBo;
import com.sqx.modules.bar.domain.vo.UserVo;
import com.sqx.modules.bar.service.BarUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户端 - 用户/会员
 */
@RestController
@RequestMapping("/app/user")
public class AppUserController {

    private final BarUserService userService;

    public AppUserController(BarUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public Result<UserVo> login(@RequestBody UserLoginBo bo) {
        return Result.ok(userService.login(bo));
    }

    @GetMapping("/info")
    @Login
    public Result<UserVo> info() {
        return Result.ok(userService.info(UserContext.getUserId()));
    }
}

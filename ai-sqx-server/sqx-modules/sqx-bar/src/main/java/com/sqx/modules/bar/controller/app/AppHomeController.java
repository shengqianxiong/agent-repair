package com.sqx.modules.bar.controller.app;

import com.sqx.common.Result;
import com.sqx.common.annotation.Login;
import com.sqx.common.utils.UserContext;
import com.sqx.modules.bar.domain.vo.HomeIndexVo;
import com.sqx.modules.bar.service.BarHomeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户端 - 首页
 */
@RestController
@RequestMapping("/app/home")
public class AppHomeController {

    private final BarHomeService homeService;

    public AppHomeController(BarHomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping("/index")
    @Login
    public Result<HomeIndexVo> index() {
        return Result.ok(homeService.index(UserContext.getUserId()));
    }
}

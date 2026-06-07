package com.sqx.modules.bar.controller.app;

import com.sqx.common.PageResult;
import com.sqx.common.Result;
import com.sqx.common.annotation.Login;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户端 - 通用与首页接口
 */
@RestController
public class AppCommonController {

    @PostMapping("/app/common/upload")
    @Login
    public Result<Map<String, String>> upload(@RequestBody Map<String, Object> body) {
        Map<String, String> data = new HashMap<>();
        data.put("url", "https://cdn.uviewui.com/uview/album/1.jpg");
        return Result.ok(data);
    }

    @GetMapping("/app/config/version")
    @Login
    public Result<Map<String, String>> version() {
        Map<String, String> data = new HashMap<>();
        data.put("version", "3.4.0");
        return Result.ok(data);
    }

    @GetMapping("/app/home/init")
    @Login
    public Result<Map<String, Object>> homeInit() {
        Map<String, Object> data = new HashMap<>();
        data.put("user", buildUser());
        data.put("activities", Collections.emptyList());
        data.put("unreadCount", 0);
        return Result.ok(data);
    }

    @GetMapping("/app/activity/list")
    @Login
    public Result<PageResult<Map<String, Object>>> activityList() {
        return Result.ok(PageResult.of(Collections.emptyList(), 0, 1, 10));
    }

    private Map<String, Object> buildUser() {
        Map<String, Object> user = new HashMap<>();
        user.put("nickname", "高级会员");
        user.put("points", 1240);
        user.put("bookingStatus", "在线预约中");
        return user;
    }
}

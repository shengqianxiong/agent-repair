package com.sqx.modules.bar.controller.app;

import com.sqx.common.Result;
import com.sqx.common.annotation.Login;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户端 - 登录与用户信息
 * 登录/注册接口禁止 RBAC，仅校验账号密码并返回 token
 */
@RestController
@RequestMapping("/app/user")
public class AppUserController {

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return Result.error("账号或密码不能为空");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("token", "uid:1");
        data.put("user", buildMockUser());
        return Result.ok(data);
    }

    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody Map<String, String> body) {
        return login(body);
    }

    @GetMapping("/info")
    @Login
    public Result<Map<String, Object>> info() {
        return Result.ok(buildMockUser());
    }

    @GetMapping("/profile")
    @Login
    public Result<Map<String, Object>> profile() {
        return Result.ok(buildMockUser());
    }

    @PostMapping("/update")
    @Login
    public Result<Void> update(@RequestBody Map<String, Object> body) {
        return Result.ok();
    }

    private Map<String, Object> buildMockUser() {
        Map<String, Object> user = new HashMap<>();
        user.put("id", 1L);
        user.put("nickname", "高级会员");
        user.put("avatar", "https://cdn.uviewui.com/uview/album/1.jpg");
        user.put("memberLevel", 5);
        user.put("memberLevelName", "LV.5 高级会员");
        user.put("points", 1240);
        user.put("balance", 1280.50);
        user.put("phone", "13800138000");
        return user;
    }
}

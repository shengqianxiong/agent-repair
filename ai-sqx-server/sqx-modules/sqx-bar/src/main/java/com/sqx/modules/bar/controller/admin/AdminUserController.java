package com.sqx.modules.bar.controller.admin;

import com.sqx.common.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理端 - 登录鉴权
 * 登录接口禁止 RBAC，仅校验账号密码并返回 token
 */
@RestController
@RequestMapping("/admin/user")
public class AdminUserController {

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return Result.error("账号或密码不能为空");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("token", "admin:1");
        data.put("username", username);
        return Result.ok(data);
    }
}

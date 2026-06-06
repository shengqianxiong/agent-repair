# 极简登录 Demo（RealCloudAgent）

## 功能

- login-server：`POST /admin/auth/login`、`POST /app/auth/login`
- login-admin：登录页 + 调用 login API
- login-app：登录页 + 调用 login API

## 约束

- 仅实现登录，不要额外业务
- 三端可编译

## 测试账号

| 端 | 用户名 | 密码 |
|---|---|---|
| 管理端 | admin | 123456 |
| 用户端 | user | 123456 |

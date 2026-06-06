# 极简登录 API 接口文档

> 服务端地址：`http://127.0.0.1:9294/sqx_fast`  
> 统一响应格式：`{ "code": 0, "msg": "success", "data": ... }`，`code !== 0` 时 `msg` 为错误提示

## 管理端 `/admin/**`

| 方法 | 路径 | 说明 | 鉴权 |
|---|---|---|---|
| POST | `/admin/user/login` | 管理员登录 | 否 |
| GET | `/admin/user/info` | 获取当前管理员信息 | token |

### 登录

- 请求：`{ "username": "admin", "password": "123456" }`
- 响应：`{ "token": "...", "id": 1, "username": "admin" }`

### 获取管理员信息

- 响应：`{ "id": 1, "username": "admin" }`

## 用户端 `/app/**`

| 方法 | 路径 | 说明 | 鉴权 |
|---|---|---|---|
| POST | `/app/user/login` | 用户登录 | 否 |
| GET | `/app/user/info` | 获取当前用户信息 | token |

### 登录

- 请求：`{ "username": "user", "password": "123456" }`
- 响应：`{ "token": "...", "id": 2, "username": "user" }`

### 获取用户信息

- 响应：`{ "id": 2, "username": "user" }`

## 鉴权说明

- 请求头：`token: <登录返回的token>`（也支持 `Authorization: Bearer <token>`）
- Token 有效期：2 小时

## 初始账号

| 用户名 | 密码 | 角色 |
|---|---|---|
| admin | 123456 | 管理员 |
| user | 123456 | 普通用户 |

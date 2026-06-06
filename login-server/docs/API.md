# 极简登录 API 接口文档

> 服务端地址：`http://127.0.0.1:9295/sqx_fast`  
> 统一响应格式：`{ "code": 0, "msg": "success", "data": ... }`，`code !== 0` 时 `msg` 为错误提示

## 管理端 `/admin/**`

| 方法 | 路径 | 说明 | 鉴权 |
|---|---|---|---|
| POST | `/admin/auth/login` | 管理员登录 | 否 |
| GET | `/admin/account/list` | 账号分页列表 | token |
| GET | `/admin/account/detail/{id}` | 账号详情 | token |
| POST | `/admin/account/save` | 新增账号 | token |
| PUT | `/admin/account/update` | 编辑账号 | token |
| DELETE | `/admin/account/delete/{id}` | 删除账号 | token |

### 登录

- 请求：`{ "account": "admin", "password": "admin123" }`
- 响应：`{ "token": "...", "id": 1, "account": "admin" }`

### 账号列表

- 参数：`account`（关键词）、`page`、`pageSize`
- 响应：`{ "list": [...], "total": 0, "page": 1, "pageSize": 10 }`
- 列表项：`{ "id", "account", "status", "statusText", "createTime" }`

### 新增账号

- 请求：`{ "account": "newuser", "password": "123456" }`

### 编辑账号

- 请求：`{ "id": 2, "password": "可选", "status": 1 }`

## 用户端 `/app/**`

| 方法 | 路径 | 说明 | 鉴权 |
|---|---|---|---|
| POST | `/app/user/login` | 用户登录 | 否 |

### 登录

- 请求：`{ "account": "user", "password": "user123" }`
- 响应：`{ "token": "...", "id": 2, "account": "user" }`

## 鉴权说明

- 管理端请求头：`token: <登录返回的token>`
- 用户端请求头：`token: <登录返回的token>`
- Token 有效期：2 小时

## 初始账号

| 账号 | 密码 | 角色 |
|---|---|---|
| admin | admin123 | 管理员 |
| user | user123 | 普通用户 |

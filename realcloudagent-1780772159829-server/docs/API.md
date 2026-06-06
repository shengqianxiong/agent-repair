# RealCloudAgent-1780772159829 API 文档

## 基础信息

| 项 | 值 |
|---|---|
| 服务地址 | `http://127.0.0.1:9294` |
| Context Path | `/sqx_fast` |
| 完整 Base URL | `http://127.0.0.1:9294/sqx_fast` |
| 响应格式 | JSON |

## 统一响应结构

```json
{
  "code": 0,
  "msg": "success",
  "data": {}
}
```

| 字段 | 类型 | 说明 |
|---|---|---|
| code | int | 0 表示成功，非 0 表示失败 |
| msg | string | 提示信息 |
| data | object | 业务数据 |

## 管理端接口（/admin/**）

### 1. 管理端登录

- **URL**：`POST /sqx_fast/admin/auth/login`
- **说明**：管理端用户登录，校验用户名与密码
- **请求头**：`Content-Type: application/json`
- **请求体**：

```json
{
  "username": "admin",
  "password": "123456"
}
```

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| username | string | 是 | 登录用户名 |
| password | string | 是 | 登录密码 |

- **成功响应**：

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "token": "admin:1",
    "userId": 1,
    "username": "admin",
    "nickname": "系统管理员"
  }
}
```

| 字段 | 类型 | 说明 |
|---|---|---|
| token | string | 访问令牌，后续请求放入 Header: `token` |
| userId | long | 管理员 ID |
| username | string | 登录用户名 |
| nickname | string | 昵称 |

- **失败响应示例**：

```json
{
  "code": 401,
  "msg": "用户名或密码错误",
  "data": null
}
```

## 用户端接口（/app/**）

### 1. 用户端登录

- **URL**：`POST /sqx_fast/app/auth/login`
- **说明**：用户端用户登录，校验用户名与密码
- **请求头**：`Content-Type: application/json`
- **请求体**：

```json
{
  "username": "user",
  "password": "123456"
}
```

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| username | string | 是 | 登录用户名 |
| password | string | 是 | 登录密码 |

- **成功响应**：

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "token": "uid:1",
    "userId": 1,
    "username": "user",
    "nickname": "演示用户"
  }
}
```

| 字段 | 类型 | 说明 |
|---|---|---|
| token | string | 访问令牌，后续请求放入 Header: `token` |
| userId | long | 用户 ID |
| username | string | 登录用户名 |
| nickname | string | 昵称 |

- **失败响应示例**：

```json
{
  "code": 401,
  "msg": "用户名或密码错误",
  "data": null
}
```

## 错误码说明

| code | 说明 |
|---|---|
| 0 | 成功 |
| 401 | 未登录或用户名/密码错误 |
| 403 | 账号被禁用或无权限 |
| 500 | 系统异常 |

# RealCloudAgent-PR-1780773063566 API 文档

## 基础信息

- **Base URL**：`http://127.0.0.1:9294/sqx_fast`
- **Content-Type**：`application/json`
- **响应格式**：

```json
{
  "code": 0,
  "msg": "success",
  "data": {}
}
```

- `code = 0` 表示成功，非 0 表示失败
- 登录成功后，后续请求在 Header 中携带 `token`

---

## 管理端接口（/admin/**）

### 1. 管理员登录

- **路径**：`POST /admin/login`
- **说明**：管理端登录，校验用户名与密码
- **是否需要登录**：否

**请求体**

```json
{
  "username": "admin",
  "password": "123456"
}
```

**成功响应 data**

```json
{
  "token": "admin:1",
  "userId": 1,
  "username": "admin",
  "nickname": "系统管理员"
}
```

**错误示例**

| code | msg |
|---|---|
| 400 | 请输入用户名和密码 |
| 401 | 用户名或密码错误 |
| 403 | 账号已被禁用，请联系管理员 |

---

## 用户端接口（/app/**）

### 1. 用户登录

- **路径**：`POST /app/user/login`
- **说明**：App 端用户登录，校验用户名与密码
- **是否需要登录**：否

**请求体**

```json
{
  "username": "user",
  "password": "123456"
}
```

**成功响应 data**

```json
{
  "token": "uid:1",
  "userId": 1,
  "username": "user",
  "nickname": "测试用户"
}
```

**错误示例**

| code | msg |
|---|---|
| 400 | 请输入用户名和密码 |
| 401 | 用户名或密码错误 |
| 403 | 账号已被禁用，请联系客服 |

---

## Token 说明

| 端 | Token 格式 | 传递方式 |
|---|---|---|
| 管理端 | `admin:{userId}` | Header: `token` |
| 用户端 | `uid:{userId}` | Header: `token` |

> 本需求仅实现登录接口；受保护接口的鉴权由各端联调时统一对齐。

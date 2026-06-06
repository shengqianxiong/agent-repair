# 极简登录 API 文档

**Base URL**：`http://127.0.0.1:9295/sqx_fast`

**统一响应格式**：

```json
{
  "code": 0,
  "msg": "success",
  "data": {}
}
```

**鉴权说明**：
- 用户端/App 接口：请求头 `token` 携带登录返回的 Token
- 管理端/Admin 接口（除登录外）：请求头 `token` 携带管理员登录返回的 Token

---

## 用户端接口（/app/**）

### 1. 用户登录

- **路径**：`POST /app/account/login`
- **鉴权**：否
- **请求体**：

```json
{
  "username": "user01",
  "password": "123456"
}
```

- **响应 data**：

```json
{
  "token": "会话Token",
  "accountId": 1,
  "username": "user01",
  "role": 0
}
```

- **错误提示**：
  - 空账号：`请输入账号`
  - 空密码：`请输入密码`
  - 校验失败：`账号或密码错误`
  - 账号禁用：`账号已被禁用`

### 2. 当前用户信息

- **路径**：`GET /app/account/info`
- **鉴权**：是（token）
- **响应 data**：账号信息（不含密码）

---

## 管理端接口（/admin/**）

### 1. 管理员登录

- **路径**：`POST /admin/account/login`
- **鉴权**：否
- **请求体**：同用户登录
- **响应 data**：同用户登录（role=1 为管理员）
- **错误提示**：
  - 非管理员：`权限不足`

### 2. 账号列表

- **路径**：`GET /admin/account/list`
- **鉴权**：是（管理员 token）
- **查询参数**：
  - `username`：账号关键词（模糊）
  - `page`：页码，默认 1
  - `pageSize`：每页条数，默认 10
- **响应 data**：分页列表 `{ list, total, page, pageSize }`

### 3. 账号详情

- **路径**：`GET /admin/account/detail/{id}`
- **鉴权**：是（管理员 token）
- **响应 data**：账号信息（不含密码）

### 4. 新增账号

- **路径**：`POST /admin/account/save`
- **鉴权**：是（管理员 token）
- **请求体**：

```json
{
  "username": "newuser",
  "password": "初始密码"
}
```

- **错误提示**：
  - 账号重复：`账号已存在`

### 5. 编辑账号

- **路径**：`PUT /admin/account/update`
- **鉴权**：是（管理员 token）
- **请求体**：

```json
{
  "id": 2,
  "password": "新密码（可选）",
  "status": 1
}
```

- **说明**：不可修改账号名称，仅可重置密码与修改状态

### 6. 删除账号

- **路径**：`DELETE /admin/account/delete/{id}`
- **鉴权**：是（管理员 token）
- **错误提示**：
  - 删除当前管理员：`不可删除当前登录的管理员账号`

# 极简登录功能 API 文档

服务地址：`http://127.0.0.1:9295/sqx_fast`

统一响应格式：

```json
{
  "code": 0,
  "msg": "success",
  "data": {}
}
```

鉴权说明：除登录接口外，管理端接口需在请求头携带 `token`（或 `Authorization: Bearer {token}`）。

---

## 管理端接口（/admin/**）

### 1. 管理员登录

- **路径**：`POST /admin/auth/login`
- **鉴权**：否
- **请求体**：

```json
{
  "username": "admin",
  "password": "admin123"
}
```

- **响应 data**：

```json
{
  "token": "会话Token",
  "accountId": 1,
  "username": "admin"
}
```

### 2. 账号列表

- **路径**：`GET /admin/account/list`
- **鉴权**：是（管理员）
- **查询参数**：`username`（可选，模糊搜索）、`page`（默认1）、`pageSize`（默认10）

### 3. 账号详情

- **路径**：`GET /admin/account/detail/{id}`
- **鉴权**：是（管理员）

### 4. 新增账号

- **路径**：`POST /admin/account/save`
- **鉴权**：是（管理员）
- **请求体**：

```json
{
  "username": "newuser",
  "password": "123456"
}
```

### 5. 编辑账号

- **路径**：`PUT /admin/account/update`
- **鉴权**：是（管理员）
- **请求体**：

```json
{
  "id": 2,
  "password": "newpass",
  "status": 1
}
```

说明：不可修改账号名称，仅可重置密码与修改状态。

### 6. 删除账号

- **路径**：`DELETE /admin/account/delete/{id}`
- **鉴权**：是（管理员）
- **限制**：不可删除当前登录的管理员账号

---

## 用户端接口（/app/**）

### 1. 用户登录

- **路径**：`POST /app/user/login`
- **鉴权**：否
- **请求体**：

```json
{
  "username": "user",
  "password": "user123"
}
```

- **响应 data**：

```json
{
  "token": "会话Token",
  "accountId": 2,
  "username": "user"
}
```

- **错误提示**：
  - 空输入：`请输入账号` / `请输入密码`
  - 凭证错误：`账号或密码错误`
  - 账号禁用：`账号已被禁用`

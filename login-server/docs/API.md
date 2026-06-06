# 极简登录功能 API 文档

**服务地址**：`http://127.0.0.1:9295/sqx_fast`

**统一响应格式**：

```json
{
  "code": 0,
  "msg": "success",
  "data": {}
}
```

- `code = 0` 表示成功，非 0 表示失败
- 管理端与需登录的用户端接口需在请求头携带 `token`

---

## 管理端接口 `/admin/**`

### 1. 管理员登录

- **路径**：`POST /admin/login`
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
  "expireAt": 1710000000000,
  "accountId": 1,
  "username": "admin"
}
```

- **错误提示**：
  - 账号或密码错误
  - 账号已被禁用
  - 无访问权限（非管理员）

### 2. 账号列表

- **路径**：`GET /admin/account/list`
- **鉴权**：是（管理员 Token）
- **查询参数**：
  - `keyword`：账号关键词（模糊）
  - `page`：页码，默认 1
  - `pageSize`：每页条数，默认 10

### 3. 账号详情

- **路径**：`GET /admin/account/detail/{id}`
- **鉴权**：是（管理员 Token）

### 4. 新增账号

- **路径**：`POST /admin/account/save`
- **鉴权**：是（管理员 Token）
- **请求体**：

```json
{
  "username": "newuser",
  "password": "123456"
}
```

- **错误提示**：账号已存在

### 5. 编辑账号

- **路径**：`PUT /admin/account/update`
- **鉴权**：是（管理员 Token）
- **请求体**：

```json
{
  "id": 2,
  "password": "newpass",
  "status": 1
}
```

- **说明**：`password`、`status` 至少传一项；不可修改账号名称

### 6. 删除账号

- **路径**：`DELETE /admin/account/delete/{id}`
- **鉴权**：是（管理员 Token）
- **错误提示**：不可删除当前登录的管理员账号

---

## 用户端接口 `/app/**`

### 1. 用户登录

- **路径**：`POST /app/login`
- **鉴权**：否
- **请求体**：

```json
{
  "username": "user01",
  "password": "admin123"
}
```

- **响应 data**：同管理端登录

### 2. 当前账号列表

- **路径**：`GET /app/account/list`
- **鉴权**：是（用户 Token）
- **说明**：仅返回当前登录用户本人

### 3. 当前账号详情

- **路径**：`GET /app/account/detail`
- **鉴权**：是（用户 Token）

---

## 初始化数据

执行 `db/login_account.sql` 后，默认管理员账号：

| 账号 | 密码 | 角色 |
|------|------|------|
| admin | admin123 | 管理员 |

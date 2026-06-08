# API 文档：VerifyDevPlan-1780929413041

## 通用约定

- **Base URL**：`http://127.0.0.1:9294/sqx_fast`
- **统一响应格式**：

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

- **鉴权**：除登录接口外，所有请求需在 Header 中携带 `token`
- **错误码**：`0` 成功；`401` 未登录；`400` 参数/业务错误

---

## App 端接口

### 1. 用户登录

- **路径**：`POST /app/login`
- **鉴权**：否
- **请求体**：

```json
{
  "username": "demo",
  "password": "123456"
}
```

- **响应 data**：

```json
{
  "token": "verifydevplan_mock_token_2026",
  "userInfo": {
    "id": 1,
    "username": "demo",
    "nickname": "演示用户",
    "avatar": "https://cdn.uviewui.com/uview/album/1.jpg"
  }
}
```

### 2. 获取商品列表

- **路径**：`GET /app/products`
- **鉴权**：是（Header: `token`）
- **Query 参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | number | 否 | 页码，默认 1 |
| pageSize | number | 否 | 每页条数，默认 20 |
| keyword | string | 否 | 商品名称关键词 |

- **响应 data**：

```json
{
  "list": [
    {
      "id": 1,
      "name": "经典款旅行背包",
      "price": 299.0,
      "originalPrice": 399.0,
      "image": "https://cdn.uviewui.com/uview/album/1.jpg",
      "description": "轻便耐用，适合短途旅行",
      "stock": 120,
      "status": 1
    }
  ],
  "total": 5,
  "page": 1
}
```

### 3. 获取商品详情

- **路径**：`GET /app/products/:id`
- **鉴权**：是（Header: `token`）
- **路径参数**：`id` 商品 ID
- **响应 data**：

```json
{
  "id": 1,
  "name": "经典款旅行背包",
  "price": 299.0,
  "originalPrice": 399.0,
  "image": "https://cdn.uviewui.com/uview/album/1.jpg",
  "description": "轻便耐用，适合短途旅行",
  "stock": 120,
  "status": 1
}
```

---

## Admin 端接口（预留）

| 功能 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 管理员登录 | POST | `/admin/login` | 本次未实现，路径预留 |

---

## 数据库

- MySQL 库名：`travel`

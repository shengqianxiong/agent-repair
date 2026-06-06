# RealCloudAgent API 文档

基础地址：`http://127.0.0.1:9294/sqx_fast`

统一响应格式：

```json
{
  "code": 0,
  "msg": "success",
  "data": {}
}
```

`code === 0` 表示成功。

## 管理端

### POST /admin/auth/login

管理端登录。

**请求体**

```json
{
  "username": "admin",
  "password": "123456"
}
```

**响应 data**

```json
{
  "token": "admin:1",
  "username": "admin"
}
```

## 用户端

### POST /app/auth/login

用户端登录。

**请求体**

```json
{
  "username": "user",
  "password": "123456"
}
```

**响应 data**

```json
{
  "token": "uid:1",
  "username": "user"
}
```

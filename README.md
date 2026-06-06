# RealCloudAgent-1780772159829

极简登录 Demo 三端工程。

| 端 | 目录 | 端口 |
|---|---|---|
| 服务端 | `realcloudagent-1780772159829-server` | 9294 |
| 管理端 | `realcloudagent-1780772159829-admin` | 5173 |
| 用户端 | `realcloudagent-1780772159829-app` | 8080 |

## 启动

```bash
# 1. 初始化数据库
mysql -u root -p travel < realcloudagent-1780772159829-server/db/auth.sql

# 2. 启动服务端
cd realcloudagent-1780772159829-server
mvn -pl sqx-admin spring-boot:run

# 3. 启动管理端
cd realcloudagent-1780772159829-admin
npm run dev

# 4. 启动用户端
cd realcloudagent-1780772159829-app
npm run dev:h5
```

## 测试账号

- 管理端：`admin` / `123456`
- 用户端：`user` / `123456`

# RealCloudAgent-1780772159829 极简登录 Demo

## 项目概述

三端协同的极简登录演示项目，仅实现登录功能，不包含其他业务模块。

| 端 | 目录 | 端口 | 技术栈 |
|---|---|---|---|
| 服务端 | `realcloudagent-1780772159829-server` | 9294 | Spring Boot 2.6 Maven，Context `/sqx_fast` |
| 管理端 | `realcloudagent-1780772159829-admin` | 5173 | Vue 3 + Vite + Element Plus |
| 用户端 | `realcloudagent-1780772159829-app` | 8080 | uni-app + Vue 3 + uview-plus |

## 功能范围

### 服务端（login-server）

- `POST /admin/auth/login` — 管理端登录
- `POST /app/auth/login` — 用户端登录

### 管理端（login-admin）

- 登录页面
- 调用管理端登录 API，保存 Token

### 用户端（login-app）

- 登录页面
- 调用用户端登录 API，保存 Token

## 约束

- **仅实现登录**，不扩展其他业务功能
- 三端均可独立编译运行
- 默认测试账号密码均为 `123456`

## 数据库

- 数据库名：`travel`
- DDL 脚本：`db/auth.sql`
- 管理端默认账号：`admin` / `123456`
- 用户端默认账号：`user` / `123456`

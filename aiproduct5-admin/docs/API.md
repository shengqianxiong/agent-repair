# aiProduct5 管理端 API 文档

Base URL: `/sqx_fast`  
鉴权 Header: `token`（登录接口除外）

## 鉴权

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/admin/seller/login` | 商家登录，返回 token |

## 工作台

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/admin/seller/dashboard` | 工作台统计（活动数、扫码量、提交数、核销量、返利支出） |
| GET | `/admin/seller/dashboard/recent-activities` | 最近活动概览 |
| GET | `/admin/seller/dashboard/recent-verifies` | 最近审核记录 |

## 活动管理

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/admin/activity/page` | 活动分页列表 |
| GET | `/admin/activity/detail` | 活动详情 |
| POST | `/admin/activity/create` | 创建活动 |
| PUT | `/admin/activity/update` | 编辑活动 |
| PUT | `/admin/activity/status` | 上下架活动 |
| POST | `/admin/activity/ai-generate` | AI 生成文案 |
| GET | `/admin/activity/stats` | 活动统计数据 |
| GET | `/admin/activity/qrcode` | 获取二维码 |

## 核销管理

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/admin/verify/page` | 核销审核分页列表 |
| GET | `/admin/verify/detail` | 审核详情 |
| POST | `/admin/verify/approve` | 通过审核 |
| POST | `/admin/verify/reject` | 拒绝审核 |

## AI 生成记录

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/admin/ai/history/page` | AI 生成记录分页 |
| GET | `/admin/ai/history/detail` | 生成记录详情 |
| POST | `/admin/ai/history/retry` | 重新生成 |

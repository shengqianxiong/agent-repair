# aiProduct5 接口文档

## 通用约定

- Base URL：`http://127.0.0.1:9294/sqx_fast`
- 鉴权：除登录/注册外，请求 header 统一携带 `token`
- 响应：`{ "code": 0, "msg": "success", "data": {} }`
- 用户端接口前缀：`/app`
- 管理端接口前缀：`/admin`

## App 消费者端接口

### GET `/app/activity/by-code`

根据二维码 code 获取评价活动。

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| code | string | 是 | 二维码活动编码 |

返回：活动 ID、商品信息、返利金额、有效期、AI 文案列表、推荐图片、抖音跳转链接。

### POST `/app/task/start`

记录用户扫码并开始任务。

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| activityId | number/string | 是 | 活动 ID |
| userIdentifier | string | 是 | 用户端匿名标识 |

返回：`taskId`、任务状态。

### POST `/app/task/copy-report`

上报复制文案行为。

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| taskId | number/string | 是 | 任务 ID |
| copyTextId | number/string | 是 | 文案 ID |

返回：成功状态。

### POST `/app/task/submit`

提交评价截图审核。

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| taskId | number/string | 是 | 任务 ID |
| activityId | number/string | 是 | 活动 ID |
| screenshotUrl | string | 是 | 评价截图 URL 或上传后的文件路径 |
| remark | string | 否 | 备注 |
| contact | string | 否 | 联系方式 |
| userIdentifier | string | 是 | 用户端匿名标识 |

返回：审核单 ID、审核状态。

### GET `/app/task/detail`

查询任务详情。

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| taskId | number/string | 是 | 任务 ID |

返回：活动信息、截图、审核状态、返利状态。

### GET `/app/user/summary`

查询个人中心统计。

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| userIdentifier | string | 是 | 用户端匿名标识 |

返回：累计返利、待审核数量、已到账金额、任务数量。

### GET `/app/user/tasks`

查询评价记录列表。

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| page | number | 否 | 页码 |
| pageSize | number | 否 | 每页条数 |
| status | string | 否 | `auditing` / `paid` / `rejected` |
| userIdentifier | string | 是 | 用户端匿名标识 |

返回：任务列表、审核状态、返利金额、拒绝原因。

### GET `/app/rebate/detail`

查询返利详情。

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| rebateId | number/string | 是 | 返利 ID |

返回：金额、状态、到账时间、关联任务、审核结果。

## Admin 商家/管理端接口

### GET `/admin/seller/dashboard`

商家工作台统计。入参：时间范围。返回：活动数、扫码量、提交数、核销数、返利支出。

### POST `/admin/activity/create`

创建活动。入参：商品链接/ID、图片、文案、返利金额、有效期。返回：活动 ID、二维码 code、二维码 URL。

### PUT `/admin/activity/update`

编辑活动基础配置。入参：活动 ID、商品配置、文案、图片、返利金额、上下架状态。返回：更新结果。

### GET `/admin/activity/list`

查询活动列表。入参：关键词、状态、分页。返回：活动列表、扫码量、提交量、状态。

### GET `/admin/activity/detail`

查询活动详情。入参：活动 ID。返回：活动数据、二维码、文案图片预览、扫码与核销统计。

### POST `/admin/activity/status`

活动上下架。入参：活动 ID、状态。返回：处理结果。

### GET `/admin/verify/list`

查询核销审核列表。入参：状态、关键词、分页。返回：待审核截图、用户标识、活动和返利信息。

### POST `/admin/verify/pass`

审核通过。入参：审核单 ID。返回：返利状态。

### POST `/admin/verify/reject`

审核拒绝。入参：审核单 ID、拒绝原因。返回：审核结果。

### GET `/admin/ai/history`

查询 AI 生成记录。入参：活动 ID、状态、分页。返回：文案、图片、生成状态、失败原因。

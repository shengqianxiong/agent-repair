# aiProduct2 接口清单（Admin + App）

> 约定：除登录/注册接口外，全部请求 Header 携带 `token`。  
> 前缀：管理端 `/sqx_fast/admin/**`，用户端 `/sqx_fast/app/**`。  
> 登录接口禁止接入菜单权限、Shiro `@RequiresPermissions`、RBAC。

## 1. 统一响应结构

```json
{
  "code": 0,
  "msg": "success",
  "data": {}
}
```

## 2. Admin 端接口

### 2.1 认证与账号
| 接口 | 方法 | 路径 | 说明 |
| --- | --- | --- | --- |
| 管理员登录 | POST | `/sqx_fast/admin/user/login` | 账号密码登录，返回 token |
| 退出登录 | POST | `/sqx_fast/admin/user/logout` | 失效当前 token |

### 2.2 数据看板
| 接口 | 方法 | 路径 | 说明 |
| --- | --- | --- | --- |
| 看板汇总 | GET | `/sqx_fast/admin/dashboard/summary` | 订单量/回收重量/用户增长 |
| 趋势图数据 | GET | `/sqx_fast/admin/dashboard/trend` | 按天统计 |
| 对账概览 | GET | `/sqx_fast/admin/dashboard/reconciliation` | 第三方对账摘要 |

### 2.3 订单管理
| 接口 | 方法 | 路径 | 说明 |
| --- | --- | --- | --- |
| 订单分页查询 | GET | `/sqx_fast/admin/recycle/order/page` | 多条件查询 |
| 订单详情 | GET | `/sqx_fast/admin/recycle/order/detail` | 含状态流转 |
| 异常处理 | POST | `/sqx_fast/admin/recycle/order/exception/handle` | 客服工单处理 |
| 强制取消订单 | POST | `/sqx_fast/admin/recycle/order/cancel` | 运营取消并同步第三方 |

### 2.4 内容管理
| 接口 | 方法 | 路径 | 说明 |
| --- | --- | --- | --- |
| 回收规则查询 | GET | `/sqx_fast/admin/content/rule/detail` | 前端首页规则 |
| 回收规则保存 | POST | `/sqx_fast/admin/content/rule/save` | 文案与阈值配置 |
| 奖励政策查询 | GET | `/sqx_fast/admin/content/reward-policy/detail` | 单位奖励金额 |
| 奖励政策保存 | POST | `/sqx_fast/admin/content/reward-policy/save` | 奖励比例设置 |
| 公益项目列表 | GET | `/sqx_fast/admin/content/public-welfare/page` | 公益活动配置 |
| 公益项目保存 | POST | `/sqx_fast/admin/content/public-welfare/save` | 新增/编辑项目 |
| FAQ 列表 | GET | `/sqx_fast/admin/content/faq/page` | 常见问题 |
| FAQ 保存 | POST | `/sqx_fast/admin/content/faq/save` | 新增/编辑 |

### 2.5 合作方管理
| 接口 | 方法 | 路径 | 说明 |
| --- | --- | --- | --- |
| 合作平台分页 | GET | `/sqx_fast/admin/partner/page` | 平台资质信息 |
| 合作平台保存 | POST | `/sqx_fast/admin/partner/save` | 新增/编辑合作方 |
| 服务区域配置 | POST | `/sqx_fast/admin/partner/service-area/save` | 维护行政区覆盖 |
| 接口开关更新 | POST | `/sqx_fast/admin/partner/api-switch/update` | 第三方同步开关 |

### 2.6 权限与审计
| 接口 | 方法 | 路径 | 说明 |
| --- | --- | --- | --- |
| 运营角色列表 | GET | `/sqx_fast/admin/system/role/page` | 角色查询 |
| 角色授权 | POST | `/sqx_fast/admin/system/role/grant` | 人员授权 |
| 操作日志分页 | GET | `/sqx_fast/admin/system/log/page` | 审计日志 |

## 3. App 端接口

### 3.1 登录与用户
| 接口 | 方法 | 路径 | 说明 |
| --- | --- | --- | --- |
| 用户登录 | POST | `/sqx_fast/app/user/login` | 账号密码登录，返回 token |
| 用户注册 | POST | `/sqx_fast/app/user/register` | 手机号注册 |
| 退出登录 | POST | `/sqx_fast/app/user/logout` | token 失效 |
| 用户信息 | GET | `/sqx_fast/app/user/profile` | 个人中心基础信息 |

### 3.2 首页与服务范围
| 接口 | 方法 | 路径 | 说明 |
| --- | --- | --- | --- |
| 首页聚合数据 | GET | `/sqx_fast/app/recycle/home` | Banner/规则/奖励/公益项目 |
| 地址服务范围校验 | POST | `/sqx_fast/app/recycle/service-area/check` | 判断是否可预约 |

### 3.3 预约与订单
| 接口 | 方法 | 路径 | 说明 |
| --- | --- | --- | --- |
| 创建预约订单 | POST | `/sqx_fast/app/recycle/order/create` | 提交预约信息 |
| 订单列表 | GET | `/sqx_fast/app/recycle/order/list` | 按状态筛选 |
| 订单详情 | GET | `/sqx_fast/app/recycle/order/detail` | 含物流轨迹 |
| 取消订单 | POST | `/sqx_fast/app/recycle/order/cancel` | 选择取消原因并同步第三方 |
| 物流轨迹 | GET | `/sqx_fast/app/recycle/order/tracking` | 快递轨迹 |

### 3.4 奖励中心
| 接口 | 方法 | 路径 | 说明 |
| --- | --- | --- | --- |
| 奖励汇总 | GET | `/sqx_fast/app/reward/summary` | 余额、累计奖励、累计重量 |
| 奖励流水 | GET | `/sqx_fast/app/reward/logs` | 收入支出明细 |
| 提现申请 | POST | `/sqx_fast/app/reward/withdraw/apply` | 微信零钱提现 |
| 礼品列表 | GET | `/sqx_fast/app/reward/gift/list` | 礼品兑换池 |
| 礼品兑换 | POST | `/sqx_fast/app/reward/gift/redeem` | 积分兑换 |

### 3.5 公益服务
| 接口 | 方法 | 路径 | 说明 |
| --- | --- | --- | --- |
| 最新证书 | GET | `/sqx_fast/app/public-welfare/certificate/latest` | 获取最新证书 |
| 证书列表 | GET | `/sqx_fast/app/public-welfare/certificate/list` | 历史证书 |
| 证书下载 | GET | `/sqx_fast/app/public-welfare/certificate/download` | 下载电子证书 |

### 3.6 地址管理
| 接口 | 方法 | 路径 | 说明 |
| --- | --- | --- | --- |
| 地址列表 | GET | `/sqx_fast/app/address/list` | 我的地址 |
| 地址保存 | POST | `/sqx_fast/app/address/save` | 新增/编辑地址 |
| 删除地址 | POST | `/sqx_fast/app/address/delete` | 删除指定地址 |
| 默认地址设置 | POST | `/sqx_fast/app/address/default/set` | 设置默认地址 |

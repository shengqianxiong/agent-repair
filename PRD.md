# E2E-AutoPRD-1781188447795 产品需求文档（PRD）

## 1. 项目概述

### 1.1 项目信息

| 项 | 内容 |
|---|---|
| 项目名称 | E2E-AutoPRD-1781188447795 |
| 项目模式 | 新建项目，统一默认架构 |
| 数据库 | MySQL：`travel` |
| Scaffold Git | `https://github.com/shengqianxiong/agent-repair.git` |
| 分支/Ref | `agent` |

### 1.2 三端架构约束

> 以下目录名、端口、上下文路径、技术栈为强约束，开发 Agent 不得擅自修改目录结构与依赖版本。

| 端 | 目录 | 端口 | 技术栈与路径 |
|---|---|---|---|
| 服务端 | `e2e-autoprd-1781188447795-server` | 9294 | Spring Boot 2.6 Maven，Context Path：`/sqx_fast` |
| 管理端 | `e2e-autoprd-1781188447795-admin` | 5173 | Vue 3 + Vite + Element Plus，页面路由：`/admin/**` |
| 移动端 | `e2e-autoprd-1781188447795-app` | 8080 | uni-app + Vue 3 + uview-plus，页面路由：`/app/**`，H5 port 8080 |

### 1.3 默认代码来源

- 服务端参考目录：`/upload/delivery-scaffold-template/2026/06/11/default-7a800f36/extracted/ai-server`
- 管理端参考目录：`/upload/delivery-scaffold-template/2026/06/11/default-7a800f36/extracted/ai-admin`
- 移动端参考目录：`/upload/delivery-scaffold-template/2026/06/11/default-7a800f36/extracted/ai-app`
- 原始 zip：`/upload/delivery-scaffold-template/2026/06/11/default-7a800f36/source.zip`

### 1.4 鉴权与登录约定

- 全站 API 使用 Header `token` 鉴权，登录与注册接口除外。
- 管理端登录接口必须位于：`POST /sqx_fast/admin/**/login`。
- 用户端登录接口必须位于：`POST /sqx_fast/app/**/login`。
- 管理端登录与用户端登录必须分开实现。
- 登录/注册接口禁止接入菜单权限、Shiro `@RequiresPermissions`、RBAC，仅校验账号密码并返回 token。
- 统一响应体建议：`{ "code": 0, "msg": "success", "data": {} }`；业务失败时 `code != 0` 且 `msg` 返回明确错误原因。

## 2. 背景与目标

### 2.1 业务背景

项目面向酒吧/餐饮/社交复合场景，覆盖用户自助点餐、堂食座位互动、存酒取酒、钱包资金、积分商城、分销邀请、会员中心、社交广场与后台运营管理。识图稿呈现的是移动端核心体验，包含首页、点餐、订单、钱包、积分、分销、存酒、社交、桌台互动、资料编辑等页面。后台需支撑这些移动端业务的运营配置、内容审核、订单处理和资金审核。

### 2.2 产品目标

1. 打通移动端用户从首页浏览、点餐下单、支付、取餐码展示到订单复购的完整链路。
2. 支持用户在门店场景中进行座位互动、附近社交、动态发布、个人主页展示与私信。
3. 支持会员资产、积分、钱包、分销收益、存酒与取酒等高频会员运营能力。
4. 管理端提供门店、桌台、商品、订单、存酒、钱包、积分、分销、动态、活动、通知等后台运营能力。
5. 前后端协议统一，Admin/App 接口路径隔离，数据结构互通，UI 严格遵循识图稿 designTokens 和页面 layout/components。

### 2.3 非目标

- 不引入复杂 RBAC、菜单权限或 Shiro 权限注解到登录/注册链路。
- 不改变默认三端工程目录、端口、依赖版本与上下文路径。
- 不开发 PRD 范围外的额外业务模块。

## 3. 用户角色

| 角色 | 端 | 说明 | 核心诉求 |
|---|---|---|---|
| 游客/未登录用户 | 移动端 | 未登录访问者 | 登录、注册后进入业务 |
| 普通会员 | 移动端 | 已注册用户 | 点餐、支付、取餐、订单、钱包、积分、存酒、资料维护 |
| 超级会员 | 移动端 | 开通会员权益用户 | 会员价、权益、积分成长、社交曝光 |
| 分销用户 | 移动端 | 参与邀请返利的会员 | 查看收益、邀请好友、收益提现 |
| 社交用户 | 移动端 | 发布动态和互动的会员 | 广场浏览、发帖、点赞、私信、个人主页 |
| 门店运营 | 管理端 | 处理商品、订单、存酒、桌台 | 维护商品与门店，处理订单和取酒 |
| 平台财务 | 管理端 | 处理钱包与提现 | 查询流水，审核提现，结算返利 |
| 内容运营 | 管理端 | 运营活动、通知、动态审核 | 发布活动，审核动态，发送公告 |
| 系统管理员 | 管理端 | 后台账号与总览管理 | 登录后台，查看经营数据，管理基础资料 |

## 4. 功能清单

### 4.1 移动端功能

| 模块 | 功能 | 页面归属 |
|---|---|---|
| 登录注册 | 手机号/账号密码登录、注册、协议勾选、token 保存 | `/app/pages/auth/login` |
| 首页 | 会员欢迎 Banner、核心入口、功能宫格、精选活动、悬浮发布 | `/app/pages/home/index` |
| 点餐 | 分类浏览、商品列表、规格选择、购物车、确认订单、支付、取餐码 | `/app/pages/menu/index`、订单相关页 |
| 订单 | 订单列表、状态筛选、制作中进度、详情、取消、再来一单、评价 | `/app/pages/order/list`、`/app/pages/order/detail` |
| 存酒 | 搜索筛选、存酒卡、立即取酒、续存申请 | `/app/pages/stored-wine/index` |
| 钱包 | 资产总览、提现、充值、资金明细、提现记录、提现账号 | `/app/pages/wallet/index` |
| 积分 | 积分总览、明细、积分商城、兑换确认、签到 | `/app/pages/points/index`、`/app/pages/points/mall` |
| 分销 | 收益总览、邀请人数、待入账、邀请海报、榜单、提现到钱包 | `/app/pages/distribution/index` |
| 会员中心 | 头像等级、会员横幅、快捷入口、积分进度、设置列表、底部 Tabbar | `/app/pages/mine/index` |
| 资料 | 头像、昵称、性别、年龄、电话、简介、相册、二维码、附近展示开关 | `/app/pages/profile/edit` |
| 社交广场 | 场景筛选、频道切换、瀑布流、详情、发布、点赞、评论 | `/app/pages/square/index` |
| 个人主页 | 用户资料卡、私信、动态瀑布流 | `/app/pages/social/user-home` |
| 桌面互动 | 桌号状态、座位统计、圆桌座位图、选择/取消座位 | `/app/pages/table/index` |
| 活动与帮助 | 活动列表/详情、通知、帮助中心、预约 | 首页入口及二级页 |

### 4.2 管理端功能

| 模块 | 功能 | 页面归属 |
|---|---|---|
| 登录 | 管理员账号密码登录，返回 token | `/admin/login` |
| 数据看板 | 经营数据、订单、收入、用户、商品、待处理事项 | `/admin/dashboard` |
| 用户管理 | 用户列表、筛选、详情、启用/禁用、资料查看 | `/admin/users`、`/admin/users/:id` |
| 门店管理 | 门店列表、新增、编辑、删除、营业状态 | `/admin/stores`、`/admin/stores/edit` |
| 桌台管理 | 桌台列表、座位状态、新增、编辑、预约/空闲/停用 | `/admin/tables` |
| 商品分类 | 分类列表、新增、编辑、删除、排序、上下架 | `/admin/product-categories` |
| 商品管理 | 商品列表、新增、编辑、上下架、库存、规格 | `/admin/products`、`/admin/products/edit` |
| 订单管理 | 订单列表、详情、制作状态流转、核销取餐码、取消/退款 | `/admin/orders`、`/admin/orders/:id` |
| 存酒管理 | 存酒记录、取酒核销、续存、剩余量和到期时间维护 | `/admin/stored-wines` |
| 钱包流水 | 资金流水查询、方向/类型/状态筛选 | `/admin/wallet/transactions` |
| 提现审核 | 提现申请列表、通过、驳回、记录追踪 | `/admin/wallet/withdraws` |
| 积分流水 | 积分增减明细查询 | `/admin/points/records` |
| 积分商品 | 积分商品列表、新增、编辑、上下架、库存 | `/admin/points/goods` |
| 兑换订单 | 积分兑换订单处理、发放、取消 | `/admin/points/exchange-orders` |
| 分销管理 | 邀请记录、返利记录、返利结算 | `/admin/distribution` |
| 动态审核 | 广场动态审核、驳回、删除违规内容 | `/admin/posts` |
| 活动管理 | 精选活动新增、编辑、发布/下线 | `/admin/activities` |
| 通知公告 | 通知列表、发送通知、消息管理 | `/admin/notifications` |

## 5. 设计规范（designTokens，需完整保留）

```json
{
  "designTokens": {
    "colors": {
      "primary": "#6F35E8",
      "primaryDark": "#5B22D6",
      "primaryLight": "#9B63FF",
      "primaryGradient": "linear-gradient(135deg,#6F35E8 0%,#9B63FF 100%)",
      "secondary": "#F1E8FF",
      "secondaryDeep": "#E4D6FA",
      "background": "#FAF8FC",
      "pageBackground": "#F8F5FA",
      "card": "#FFFFFF",
      "surfaceMuted": "#F4F0F6",
      "textPrimary": "#201B2D",
      "textRegular": "#5D5669",
      "textSecondary": "#8D8798",
      "textPlaceholder": "#B8B1C4",
      "border": "#EEE7F6",
      "success": "#15C887",
      "warning": "#FFB547",
      "danger": "#E85063",
      "price": "#6F35E8",
      "disabled": "#D9D3E3",
      "white": "#FFFFFF",
      "black": "#000000"
    },
    "typography": {
      "fontFamily": "system-ui,-apple-system,BlinkMacSystemFont,'PingFang SC','Microsoft YaHei',sans-serif",
      "navTitle": { "fontSize": 18, "lineHeight": 24, "fontWeight": 700 },
      "pageTitle": { "fontSize": 26, "lineHeight": 34, "fontWeight": 800 },
      "sectionTitle": { "fontSize": 20, "lineHeight": 28, "fontWeight": 800 },
      "cardTitle": { "fontSize": 17, "lineHeight": 24, "fontWeight": 700 },
      "body": { "fontSize": 14, "lineHeight": 22, "fontWeight": 400 },
      "bodyStrong": { "fontSize": 15, "lineHeight": 22, "fontWeight": 700 },
      "caption": { "fontSize": 12, "lineHeight": 18, "fontWeight": 400 },
      "tag": { "fontSize": 11, "lineHeight": 16, "fontWeight": 600 },
      "priceLarge": { "fontSize": 32, "lineHeight": 40, "fontWeight": 800 },
      "price": { "fontSize": 24, "lineHeight": 32, "fontWeight": 800 },
      "numberHero": { "fontSize": 48, "lineHeight": 56, "fontWeight": 900 }
    },
    "spacing": {
      "xs": 4,
      "sm": 8,
      "md": 12,
      "lg": 16,
      "xl": 20,
      "xxl": 24,
      "section": 32,
      "pagePadding": 18,
      "cardPadding": 18,
      "bottomBarHeight": 72,
      "tabBarHeight": 64
    },
    "radius": {
      "xs": 6,
      "sm": 10,
      "md": 14,
      "lg": 18,
      "xl": 24,
      "pill": 999,
      "circle": "50%"
    },
    "shadows": {
      "card": "0 8px 24px rgba(111,53,232,0.10)",
      "primaryButton": "0 8px 18px rgba(111,53,232,0.28)",
      "float": "0 10px 28px rgba(111,53,232,0.24)",
      "soft": "0 4px 16px rgba(35,20,70,0.06)"
    }
  }
}
```

### 5.1 UI 实现原则

- 移动端页面基于 uni-app + Vue 3 + uview-plus 实现，组件优先使用 `u-navbar`、`u-search`、`u-tabs`、`u-popup`、`u-button`、`u-cell`、`u-switch`、`u-upload`、`u-form`、`u-grid`、`u-badge`、`u-icon` 等。
- 管理端页面基于 Vue 3 + Element Plus 实现，表格、表单、弹窗、抽屉、分页、筛选区优先使用 `el-table`、`el-form`、`el-dialog`、`el-drawer`、`el-pagination`、`el-date-picker`、`el-select`、`el-upload`。
- 移动端所有主按钮统一使用 `primaryGradient`，圆角优先使用 `radius.pill` 或 `radius.lg`，卡片使用 `card` 背景与 `shadows.card`。
- 页面背景优先使用 `background` 或 `pageBackground`，分割线和描边使用 `border`。
- 价格、积分、取餐号等重点数字使用 `price`、`priceLarge` 或 `numberHero` 字体规格。

## 6. 移动端页面需求（完整保留 layout/components）

### 6.1 页面清单

| ID | 页面 | 路由 | 来源 | 一级/二级 |
|---|---|---|---|---|
| mobile-home | 首页 | `/app/pages/home/index` | 识图 | 一级 |
| mobile-menu | 点餐商品列表 | `/app/pages/menu/index` | 识图 | 一级 |
| mobile-square | 广场动态 | `/app/pages/square/index` | 识图 | 一级 |
| mobile-mine | 我的/会员中心 | `/app/pages/mine/index` | 识图 | 一级 |
| mobile-login-register | 登录/注册 | `/app/pages/auth/login` | 推断补全 | 二级 |
| mobile-product-detail | 商品详情/规格弹窗 | `/app/pages/product/detail` | 推断补全 | 二级 |
| mobile-cart-popup | 购物车弹层 | `/app/components/cart-popup` | 推断补全 | 弹层 |
| mobile-order-confirm | 确认订单 | `/app/pages/order/confirm` | 识图 | 二级 |
| mobile-payment-result | 支付结果 | `/app/pages/order/pay-result` | 推断补全 | 二级 |
| mobile-order-detail | 订单详情/取餐码 | `/app/pages/order/detail` | 识图 | 二级 |
| mobile-my-orders | 我的订单 | `/app/pages/order/list` | 识图 | 二级 |
| mobile-stored-wine-list | 我的存酒 | `/app/pages/stored-wine/index` | 识图 | 二级 |
| mobile-stored-wine-filter | 存酒筛选弹窗 | `/app/components/stored-wine-filter` | 推断补全 | 弹层 |
| mobile-wallet | 我的钱包 | `/app/pages/wallet/index` | 识图 | 二级 |
| mobile-withdraw-recharge | 提现/充值表单 | `/app/pages/wallet/withdraw` | 推断补全 | 二级 |
| mobile-points | 我的积分 | `/app/pages/points/index` | 识图 | 二级 |
| mobile-points-mall | 积分商城 | `/app/pages/points/mall` | 识图 | 二级 |
| mobile-exchange-confirm | 积分兑换确认 | `/app/pages/points/exchange-confirm` | 推断补全 | 二级 |
| mobile-distribution-center | 分销中心 | `/app/pages/distribution/index` | 识图 | 二级 |
| mobile-profile-edit | 编辑资料 | `/app/pages/profile/edit` | 识图 | 二级 |
| mobile-user-homepage | 个人主页 | `/app/pages/social/user-home` | 识图 | 二级 |
| mobile-post-detail-editor | 动态详情/发布动态 | `/app/pages/square/detail` | 推断补全 | 二级/发布页 |
| mobile-table-interaction | 桌面互动/座位状态 | `/app/pages/table/index` | 识图 | 二级 |

### 6.2 首页（mobile-home）

- 页面名称：首页
- 路由：`/app/pages/home/index`
- 页面类型：mobile
- 识图索引：imageIndex 9
- layout：
  - structure：顶部导航+会员欢迎大 Banner+双核心入口+六宫格功能+精选活动横向卡片+悬浮发布按钮+底部 tabbar
  - background：浅紫白页面，卡片化分区
  - navigation：自助点餐、在线预约、酒水套餐、附近搭子、会员中心、积分、团购核销、互动游戏、活动详情
- components：
  - 欢迎 Banner：紫色渐变大圆角，叠加半透明功能块，含会员等级徽章、在线预约状态与积分
  - 核心入口卡：两张并排卡，自助点餐为紫色渐变，在线预约为白底紫字
  - 功能宫格：2 行 3 列圆角白色容器，线性紫色图标
  - 精选活动卡：横向滑动活动卡，图片上方标签，标题、说明、价格与立即抢购按钮
  - 悬浮加号：右下紫色圆形按钮，阴影明显
- uview-plus 建议：
  - `u-navbar` 顶部导航，`u-swiper` 或横向 `scroll-view` 展示活动。
  - 功能宫格使用 `u-grid`/`u-grid-item`。
  - 悬浮发布按钮使用固定定位 `view` + `u-icon`。
- 数据：
  - 调用 `/sqx_fast/app/home/summary` 获取用户等级、积分、预约状态、活动列表、快捷入口。

### 6.3 点餐商品列表（mobile-menu）

- 页面名称：点餐商品列表
- 路由：`/app/pages/menu/index`
- 页面类型：mobile
- 识图索引：imageIndex 8
- layout：
  - structure：顶部栏+左侧分类竖栏+右侧商品列表+底部购物车结算栏+底部 tabbar
  - background：白色主背景，商品卡浮于浅底
  - navigation：分类切换、搜索、加入购物车、去结算
- components：
  - 分类竖栏：左侧固定宽度，选中项紫色指示条和紫色图标文字
  - 商品卡：白色圆角卡，左图右文，标题粗体、描述省略、原价大紫色、会员价粉色圆标，右侧悬浮加号按钮
  - 购物车栏：底部白色胶囊浮层，左侧购物篮带红色角标，中间总价和优惠，右侧紫色去结算按钮
- uview-plus 建议：
  - 分类使用纵向 `scroll-view`，商品使用 `scroll-view` + 自定义卡片。
  - 搜索使用 `u-search`，商品加号使用 `u-button` 圆形图标按钮。
  - 购物车弹层调用 `mobile-cart-popup`。
- 数据：
  - 分类：`GET /sqx_fast/app/products/categories`
  - 商品：`GET /sqx_fast/app/products?categoryId=&keyword=&storeId=`
  - 购物车：`GET /sqx_fast/app/cart`

### 6.4 商品详情/规格弹窗（mobile-product-detail）

- 页面名称：商品详情/规格弹窗
- 路由：`/app/pages/product/detail`
- 页面类型：mobile
- 来源：implicit
- layout：
  - structure：商品大图+名称价格+规格选择+数量步进器+加入购物车/立即购买底部栏
  - background：白色页面或底部弹层
  - navigation：从点餐商品卡进入
- components：
  - 规格选择：浅紫选中胶囊，支持温度、杯型、加料等
  - 数量步进器：紫色加减按钮
  - 底部操作栏：合计价和主按钮
- uview-plus 建议：
  - 规格弹层使用 `u-popup mode="bottom"`。
  - 规格标签使用自定义胶囊或 `u-tag`。
  - 数量使用 `u-number-box`。
- 交互：
  - 未选完整规格时加入购物车按钮置灰并提示。
  - 点击立即购买时直接进入确认订单。

### 6.5 购物车弹层（mobile-cart-popup）

- 页面名称：购物车弹层
- 路由：`/app/components/cart-popup`
- 页面类型：mobile component
- 来源：implicit
- layout：
  - structure：底部抽屉+已选商品列表+清空按钮+结算按钮
  - background：半透明遮罩与白色圆角顶边抽屉
  - navigation：从购物车浮层打开
- components：
  - 购物车商品行：商品名、规格、单价、数量步进器
  - 清空购物车：灰色文本按钮
- uview-plus 建议：
  - 使用 `u-popup`，商品行使用 `u-cell` 或自定义 flex 行。
  - 数量修改调用 `PUT /sqx_fast/app/cart/items/{id}`，清空调用 `DELETE /sqx_fast/app/cart`。

### 6.6 确认订单（mobile-order-confirm）

- 页面名称：确认订单
- 路由：`/app/pages/order/confirm`
- 页面类型：mobile
- 识图索引：imageIndex 10
- layout：
  - structure：顶部返回栏+门店/座位卡+商品清单卡+支付方式卡+备注入口
  - background：浅灰紫背景
  - navigation：返回点餐、选择支付、填写备注、提交支付
- components：
  - 门店座位卡：白色圆角卡，定位图标，门店名称地址，堂食标签，当前座位 A-08 紫色突出
  - 商品清单：商品图、标题、规格数量、价格，底部小计/打包费/合计
  - 支付方式：两行单选卡，微信支付选中有紫色描边与勾选，零钱支付显示余额
  - 订单备注入口：白卡列表项，左图标，右侧无备注和箭头
- uview-plus 建议：
  - 支付方式用 `u-radio-group`，备注用 `u-popup` 或跳转备注编辑页。
  - 底部提交按钮固定定位，使用 `primaryGradient`。
- 数据：
  - 进入时调用 `POST /sqx_fast/app/orders/preview` 计算金额。
  - 提交调用 `POST /sqx_fast/app/orders`，支付调用 `POST /sqx_fast/app/orders/{id}/pay`。

### 6.7 支付结果（mobile-payment-result）

- 页面名称：支付结果
- 路由：`/app/pages/order/pay-result`
- 页面类型：mobile
- 来源：implicit
- layout：
  - structure：状态图标+支付金额+订单入口+取餐码入口
  - background：白色卡片居中
  - navigation：支付成功后跳转订单详情
- components：
  - 成功状态：绿色圆形勾选图标
  - 主按钮：紫色查看取餐码按钮
- 交互：
  - 支付成功：展示金额、订单号，主按钮进入 `/app/pages/order/detail?id=订单ID`。
  - 支付失败：展示失败原因，提供重新支付与返回订单。

### 6.8 订单详情/取餐码（mobile-order-detail）

- 页面名称：订单详情/取餐码
- 路由：`/app/pages/order/detail`
- 页面类型：mobile
- 识图索引：imageIndex 1；imageIndex 11 为重复稿，用于确认待出餐详情视觉
- layout：
  - structure：顶部导航栏+状态提示+取餐码大卡片+门店商品卡+订单信息卡+底部固定操作栏
  - background：浅紫白背景，内容卡片纵向堆叠
  - navigation：返回、分享；底部操作含再来一单与订单详情
- components：
  - 取餐状态胶囊：浅紫底、紫色圆点，文案为等待出餐中
  - 取餐码卡片：白色大圆角卡，顶部紫色细边，取餐号 A086 使用超大紫色字体，展示等待件数与制作中数量
  - 主按钮：整宽紫色圆角按钮，阴影明显，文案向店员展示取餐码
  - 商品清单卡：门店标题带餐具图标，商品缩略图、名称、规格、数量、价格右对齐，合计金额紫色突出
  - 订单信息卡：两列标签值布局，包含订单编号、下单时间、支付方式、备注
  - 底部栏：左侧实付金额，右侧两个胶囊按钮
- 补充：
  - 重复稿强调：A086 大号紫色取餐码，等待出餐中状态；门店、商品、金额、订单编号、支付方式与备注完整展示。
- uview-plus 建议：
  - 使用 `u-navbar`、`u-sticky` 或固定底部 `view`。
  - 取餐码使用 `numberHero`，颜色 `primary`。
- 数据：
  - `GET /sqx_fast/app/orders/{id}`。

### 6.9 我的订单（mobile-my-orders）

- 页面名称：我的订单
- 路由：`/app/pages/order/list`
- 页面类型：mobile
- 识图索引：imageIndex 15
- layout：
  - structure：顶部导航+状态 tab+制作中高亮卡+订单列表+底部 tabbar
  - background：浅灰紫页面
  - navigation：状态筛选、查看详情、取餐码、再来一单、评价赢积分
- components：
  - 状态 tab：全部、制作中、待出餐、已完成、已取消，选中紫色
  - 制作中进度卡：紫色渐变大卡，展示点餐号码、前面还有位数、当前商品等待时间，右侧箭头
  - 订单卡：白色圆角卡，状态图标、时间、商品图/编号、金额、操作按钮
  - 取消订单卡：虚线边框、灰化图标与金额，突出已退款状态
- uview-plus 建议：
  - 状态 tab 使用 `u-tabs`。
  - 列表使用 `scroll-view` + 下拉刷新 + 触底分页。
- 数据：
  - `GET /sqx_fast/app/orders?status=&page=&pageSize=`

### 6.10 我的存酒（mobile-stored-wine-list）

- 页面名称：我的存酒
- 路由：`/app/pages/stored-wine/index`
- 页面类型：mobile
- 识图索引：imageIndex 2
- layout：
  - structure：顶部导航+搜索框+标题筛选+存酒卡片列表
  - background：浅灰紫页面，卡片白底
  - navigation：汉堡菜单、通知；筛选进入筛选弹层，立即取酒进入取酒确认
- components：
  - 搜索框：圆角浅灰输入框，左侧搜索图标，占满内容宽度
  - 存酒卡：顶部大图、左上剩余容量紫色标签、右上到期时间条；下方商品名、类型、存入时间和取酒按钮
  - 立即取酒按钮：紫色整宽按钮，酒瓶图标+文字
  - 续存申请按钮：灰紫次按钮，仅部分卡片出现
- uview-plus 建议：
  - 搜索用 `u-search`，筛选弹层用 `u-popup`。
  - 列表为空时使用 `u-empty`。
- 数据：
  - `GET /sqx_fast/app/stored-wines?keyword=&typeName=&status=&remainingMin=&remainingMax=&expireStart=&expireEnd=`

### 6.11 存酒筛选弹窗（mobile-stored-wine-filter）

- 页面名称：存酒筛选弹窗
- 路由：`/app/components/stored-wine-filter`
- 页面类型：mobile component
- 来源：implicit
- layout：
  - structure：右侧或底部筛选抽屉+类型/剩余容量/时间筛选+重置确认按钮
  - background：白底圆角弹层
  - navigation：我的存酒点击筛选
- components：
  - 筛选项：胶囊标签，选中紫色
  - 操作按钮：重置次按钮与确定主按钮
- 筛选项：
  - 类型：洋酒、红酒、啤酒、香槟、其他。
  - 状态：可取、临期、已取完、已过期。
  - 剩余容量：全部、>70%、30%-70%、<30%。
  - 时间：近 30 天、近 90 天、自定义到期时间。

### 6.12 我的钱包（mobile-wallet）

- 页面名称：我的钱包
- 路由：`/app/pages/wallet/index`
- 页面类型：mobile
- 识图索引：imageIndex 4
- layout：
  - structure：顶部导航+资产总览卡+功能列表卡+会员权益横幅
  - background：浅紫白渐变感页面
  - navigation：提现、充值、资金明细、提现记录、提现账号
- components：
  - 资产卡：白色大圆角阴影卡，居中展示我的资产和金额，眼睛图标控制显隐
  - 提现/充值按钮：双按钮横排，提现为紫色渐变主按钮，充值为浅紫次按钮
  - 功能入口列表：三行列表，左侧圆形图标，右侧箭头，分割线分隔
  - 会员权益卡：灰白渐变圆角横幅，右侧紫色去开启按钮
- uview-plus 建议：
  - 列表用 `u-cell-group`/`u-cell`，眼睛用 `u-icon`。
- 数据：
  - `GET /sqx_fast/app/wallet`

### 6.13 提现/充值表单（mobile-withdraw-recharge）

- 页面名称：提现/充值表单
- 路由：`/app/pages/wallet/withdraw`
- 页面类型：mobile
- 来源：implicit
- layout：
  - structure：金额输入+账户选择+手续费提示+提交按钮
  - background：浅紫白表单页
  - navigation：钱包提现/充值进入
- components：
  - 金额输入：大字号金额输入，人民币符号前缀
  - 账户选择：列表项带箭头，可进入提现账号管理
- 补充页面：
  - `/app/pages/wallet/recharge`：充值金额选择、支付方式、确认充值。
  - `/app/pages/wallet/transactions`：资金明细列表。
  - `/app/pages/wallet/withdraw-records`：提现记录列表。
  - `/app/pages/wallet/withdraw-accounts`：提现账号列表、新增/编辑账号。

### 6.14 我的积分（mobile-points）

- 页面名称：我的积分
- 路由：`/app/pages/points/index`
- 页面类型：mobile
- 识图索引：imageIndex 5
- layout：
  - structure：顶部导航+积分总览卡+积分明细列表
  - background：浅灰紫页面
  - navigation：积分商城、筛选
- components：
  - 积分总览卡：白色圆角卡，左侧当前可用积分，右侧机器人图片，内部大紫色积分商城按钮
  - 明细标题栏：左侧积分明细，右侧筛选图标文字
  - 积分记录项：白卡圆角，左侧圆形场景图标，中间行为名称和时间，右侧紫色积分变动
- uview-plus 建议：
  - 明细筛选使用 `u-popup`，记录用 `u-list` 或 `scroll-view`。
- 数据：
  - 汇总：`GET /sqx_fast/app/points`
  - 明细：`GET /sqx_fast/app/points/records?scene=&direction=&startTime=&endTime=&page=&pageSize=`

### 6.15 积分商城（mobile-points-mall）

- 页面名称：积分商城
- 路由：`/app/pages/points/mall`
- 页面类型：mobile
- 识图索引：imageIndex 14
- layout：
  - structure：顶部返回栏+积分总览 Banner+分类胶囊+热门兑换商品双列网格+签到任务卡+底部 tabbar
  - background：浅紫白背景
  - navigation：我的兑换、分类筛选、商品详情、立即兑换、去签到
- components：
  - 积分 Banner：紫色渐变大卡，展示当前可用积分与服务到期入口，右上我的兑换按钮
  - 分类胶囊：全部商品、数码周边、虚拟礼包、生活等横向滚动
  - 兑换商品卡：双列白卡，图片黑底科技感，商品名、积分值、立即兑换按钮；可显示 HOT/LIMITED 标签
  - 签到卡：底部浅灰紫卡，星形图标，提示赚取更多积分，右侧去签到按钮
- 数据：
  - `GET /sqx_fast/app/points/mall/goods?category=&keyword=&page=&pageSize=`

### 6.16 积分兑换确认（mobile-exchange-confirm）

- 页面名称：积分兑换确认
- 路由：`/app/pages/points/exchange-confirm`
- 页面类型：mobile
- 来源：implicit
- layout：
  - structure：商品信息+收货/兑换信息+积分消耗+确认兑换按钮
  - background：卡片式确认页
  - navigation：积分商品立即兑换进入
- components：
  - 积分消耗：紫色数字突出，展示兑换后剩余积分
  - 确认按钮：紫色渐变整宽按钮
- 交互：
  - 校验库存、用户积分余额、收货/兑换信息完整性。
  - 成功后生成兑换订单并扣减积分。

### 6.17 分销中心（mobile-distribution-center）

- 页面名称：分销中心
- 路由：`/app/pages/distribution/index`
- 页面类型：mobile
- 识图索引：imageIndex 6
- layout：
  - structure：顶部导航+收益总览+指标卡+邀请横幅+邀请榜单
  - background：白到浅紫背景
  - navigation：提现至钱包、邀请好友、查看全部榜单
- components：
  - 收益卡：大圆角白紫渐变卡，展示累计收益和提现胶囊按钮
  - 指标卡：两张方形白卡，图标圆底，展示邀请人数与待入账收益
  - 邀请横幅：整块紫色渐变说明卡，白色按钮带分享图标
  - 邀请榜单：顶部双 tab，用户头像、昵称、邀请日期、贡献金额、成单数列表
- uview-plus 建议：
  - 榜单 tab 用 `u-tabs`，分享按钮调起 uni.share 或海报生成。
- 数据：
  - `GET /sqx_fast/app/distribution/summary`
  - `GET /sqx_fast/app/distribution/invitees?tab=rank|records&page=&pageSize=`

### 6.18 我的/会员中心（mobile-mine）

- 页面名称：我的/会员中心
- 路由：`/app/pages/mine/index`
- 页面类型：mobile
- 识图索引：imageIndex 7
- layout：
  - structure：顶部导航+会员信息+会员开通横幅+快捷入口+积分进度+设置列表+底部 tabbar
  - background：浅灰紫页面
  - navigation：编辑资料、钱包、快捷取酒、订单中心、积分、邀请、客服、帮助、隐私
- components：
  - 会员头部：头像圆形带 LV 标签，昵称等级和 ID，右侧设置图标
  - 超级会员横幅：紫色渐变卡，左侧权益文案，右侧白底立即开通按钮
  - 快捷宫格：三列白卡，账户余额、快速取酒、订单中心
  - 积分进度卡：白卡，积分数右对齐，紫色进度条，去赚积分链接
  - 菜单列表：白色圆角分组列表，左图标右箭头
  - 底部导航：首页、点餐、排行榜、我的，当前项紫色
- uview-plus 建议：
  - 快捷入口使用 `u-grid`，菜单使用 `u-cell-group`。

### 6.19 编辑资料（mobile-profile-edit）

- 页面名称：编辑资料
- 路由：`/app/pages/profile/edit`
- 页面类型：mobile
- 识图索引：imageIndex 12
- layout：
  - structure：顶部导航+头像上传+基础表单+开关项+简介文本域+图片上传区+固定保存按钮
  - background：白色表单页
  - navigation：返回、更多、上传头像/相册/二维码、保存修改
- components：
  - 头像上传：圆形头像居中，右下紫色相机按钮，提示点击更换头像
  - 输入框：浅灰底大圆角，昵称、年龄、电话等字段
  - 性别分段控件：男/女双选，选中紫色渐变底，带性别图标
  - 附近好友展示开关：白色描边卡，右侧是/否胶囊切换
  - 图片上传框：两列虚线紫色边框上传区域，用于个人相册与微信二维码
  - 保存按钮：底部大紫色渐变整宽按钮
- uview-plus 建议：
  - 表单使用 `u-form`，上传使用 `u-upload`，开关使用 `u-switch`。
- 数据：
  - 获取：`GET /sqx_fast/app/user/profile`
  - 保存：`PUT /sqx_fast/app/user/profile`
  - 上传：`POST /sqx_fast/app/upload/image`

### 6.20 个人主页（mobile-user-homepage）

- 页面名称：个人主页
- 路由：`/app/pages/social/user-home`
- 页面类型：mobile
- 识图索引：imageIndex 3
- layout：
  - structure：顶部返回栏+个人资料卡+瀑布流动态相册
  - background：米白浅底
  - navigation：返回、更多；私信TA进入聊天
- components：
  - 资料卡：白色圆角卡，头像圆形，昵称、性别年龄、城市标签、简介，右上浅紫私信按钮
  - 动态标题：左侧 TA 的动态，右侧总篇数
  - 图片瀑布流：双列圆角图片网格，高度错落，图片角落可叠加文案
- uview-plus 建议：
  - 瀑布流可用双列 `view` 分流渲染。
- 数据：
  - `GET /sqx_fast/app/users/{id}/homepage`

### 6.21 广场动态（mobile-square）

- 页面名称：广场动态
- 路由：`/app/pages/square/index`
- 页面类型：mobile
- 识图索引：imageIndex 13
- layout：
  - structure：顶部导航+场景筛选胶囊+频道 tab+双列动态瀑布流+悬浮发布按钮+底部 tabbar
  - background：白色页面
  - navigation：附近搭子、今日热门、约个饭；推荐/附近/最新；动态详情、发布动态
- components：
  - 筛选胶囊：横向按钮，选中紫色渐变，未选中浅灰底
  - 频道 tab：推荐为紫色粗体并带下划线，其他灰色
  - 动态卡片：双列卡片，圆角图片，标题两行省略，作者头像昵称，点赞数，年龄距离标签
  - 发布按钮：右下紫色圆形加号，浮于内容上
- uview-plus 建议：
  - 场景筛选使用横向 `scroll-view`，频道用 `u-tabs`。
- 数据：
  - `GET /sqx_fast/app/posts?scene=&channel=&keyword=&city=&page=&pageSize=`

### 6.22 动态详情/发布动态（mobile-post-detail-editor）

- 页面名称：动态详情/发布动态
- 路由：`/app/pages/square/detail`
- 页面类型：mobile
- 来源：implicit
- layout：
  - structure：详情页为大图轮播+正文+作者+评论点赞；发布页为图片上传+文本输入+标签位置选择
  - background：白色页面
  - navigation：广场卡片进入详情，悬浮加号进入发布
- components：
  - 图片上传：九宫格圆角上传区
  - 互动栏：点赞、评论、分享图标按钮
- 补充路由：
  - `/app/pages/square/publish`：发布动态页。
  - `/app/pages/square/detail?id=动态ID`：动态详情页。

### 6.23 桌面互动/座位状态（mobile-table-interaction）

- 页面名称：桌面互动/座位状态
- 路由：`/app/pages/table/index`
- 页面类型：mobile
- 识图索引：imageIndex 16
- layout：
  - structure：顶部导航+桌号状态+积分与座位统计+圆桌座位图+我的入座卡+底部 tabbar
  - background：白色到浅紫背景
  - navigation：选择座位、取消入座、查看桌友、预约状态
- components：
  - 桌号标题：A.红桌 大号紫色，右侧绿色预约中状态点
  - 统计卡：两张浅紫卡，基础积分与座位状态大数字展示
  - 圆桌座位图：中心大圆桌浅紫底，周围座位卡环形排布；已入座为紫色卡带头像姓名，空位为白卡灰头像
  - 我的座位卡：底部白卡，头像带在线绿点，显示已入座 A4，右侧取消按钮
- uview-plus 建议：
  - 座位图使用绝对定位或 CSS grid 近似环形排布。
- 数据：
  - `GET /sqx_fast/app/tables/{tableId}/seats`
  - `POST /sqx_fast/app/tables/{tableId}/seats/{seatNo}/join`
  - `POST /sqx_fast/app/tables/{tableId}/seats/cancel`

### 6.24 登录/注册（mobile-login-register）

- 页面名称：登录/注册
- 路由：`/app/pages/auth/login`
- 页面类型：mobile
- 来源：implicit
- layout：
  - structure：品牌标题+手机号/密码或验证码表单+登录注册按钮+协议勾选
  - background：沿用浅紫白背景和紫色主按钮
  - navigation：登录后进入首页或我的页
- components：
  - 登录表单：圆角浅灰输入框，紫色提交按钮
  - 注册入口：文本按钮，手机号、昵称、密码基础字段
- 规则：
  - 登录/注册接口不使用 token。
  - 登录成功后保存 token，进入首页或来源页。
  - 注册需校验手机号、昵称、密码、协议勾选。

## 7. 管理端页面需求

### 7.1 管理端通用布局

- 左侧菜单 + 顶部用户信息 + 内容区。
- 所有列表页包含：筛选区、操作按钮区、表格、分页、批量操作（如适用）。
- 所有新增/编辑优先使用 `el-dialog` 或独立编辑页；详情类使用 `el-drawer` 或详情页。
- 列表页必须支持 `page`、`pageSize`，查询按钮重置为第一页，重置按钮清空筛选条件。

### 7.2 管理端页面清单与筛选条件

| 页面 | 路由 | 页面内容 | 列表筛选条件 | 排序参数 |
|---|---|---|---|---|
| 登录 | `/admin/login` | 账号、密码、登录按钮 | 无 | 无 |
| 数据看板 | `/admin/dashboard` | 订单数、收入、用户数、待处理订单、待审核提现、待审核动态 | 日期范围 `startDate,endDate`、门店 `storeId` | `statDate desc` |
| 用户列表 | `/admin/users` | 用户资料、等级、余额、积分、状态 | 关键词 `keyword`、手机号 `phone`、等级 `level`、状态 `status`、城市 `city`、注册时间 `startTime,endTime` | `createdAt desc`、`points desc`、`walletBalance desc` |
| 用户详情 | `/admin/users/:id` | 基本资料、订单、积分、钱包、动态 | 无 | 无 |
| 门店列表 | `/admin/stores` | 门店名称、地址、电话、营业时间、状态 | 关键词 `keyword`、状态 `status`、营业时间 `businessHours`、城市/地址 `addressKeyword` | `id desc`、`status asc` |
| 门店新增/编辑 | `/admin/stores/edit` | 门店表单、经纬度、状态 | 无 | 无 |
| 桌台列表 | `/admin/tables` | 桌台、容量、占用人数、预约状态、座位详情 | 关键词 `keyword`、门店 `storeId`、状态 `status`、容量范围 `capacityMin,capacityMax`、更新时间 `startTime,endTime` | `updatedAt desc`、`occupiedCount desc` |
| 商品分类 | `/admin/product-categories` | 分类名称、图标、排序、状态 | 关键词 `keyword`、状态 `status`、排序范围 `sortMin,sortMax` | `sort asc`、`id desc` |
| 商品列表 | `/admin/products` | 商品图、分类、名称、价格、会员价、库存、销量、状态 | 关键词 `keyword`、分类 `categoryId`、状态 `status`、库存状态 `stockStatus`、价格区间 `priceMin,priceMax`、销量区间 `salesMin,salesMax`、创建时间 `startTime,endTime` | `createdAt desc`、`sales desc`、`price asc/desc` |
| 商品新增/编辑 | `/admin/products/edit` | 商品基础信息、图片、规格、价格、库存 | 无 | 无 |
| 订单列表 | `/admin/orders` | 订单号、用户、门店、桌台、取餐码、状态、金额、支付状态 | 关键词 `keyword`、订单号 `orderNo`、用户 `userId`、门店 `storeId`、状态 `status`、支付状态 `payStatus`、支付方式 `paymentMethod`、下单时间 `startTime,endTime`、金额区间 `amountMin,amountMax` | `createdAt desc`、`payAmount desc` |
| 订单详情 | `/admin/orders/:id` | 商品明细、订单信息、状态流转、核销 | 无 | 无 |
| 存酒管理 | `/admin/stored-wines` | 用户、门店、酒品、剩余量、到期时间、状态、续存 | 关键词 `keyword`、用户 `userId`、门店 `storeId`、类型 `typeName`、状态 `status`、可续存 `canRenew`、剩余量 `remainingMin,remainingMax`、到期时间 `expireStart,expireEnd` | `expireAt asc`、`storedAt desc` |
| 钱包流水 | `/admin/wallet/transactions` | 用户、类型、金额、方向、状态、关联单号 | 关键词 `keyword`、用户 `userId`、类型 `type`、方向 `direction`、状态 `status`、金额区间 `amountMin,amountMax`、时间 `startTime,endTime` | `createdAt desc`、`amount desc` |
| 提现审核 | `/admin/wallet/withdraws` | 提现金额、账号、状态、审核操作 | 关键词 `keyword`、用户 `userId`、状态 `status`、账号类型 `accountType`、金额区间 `amountMin,amountMax`、申请时间 `startTime,endTime` | `createdAt desc`、`amount desc` |
| 积分流水 | `/admin/points/records` | 用户、场景、积分、方向、关联单号 | 关键词 `keyword`、用户 `userId`、场景 `scene`、方向 `direction`、积分区间 `pointsMin,pointsMax`、时间 `startTime,endTime` | `createdAt desc`、`points desc` |
| 积分商品 | `/admin/points/goods` | 商品、分类、积分价、标签、库存、状态 | 关键词 `keyword`、分类 `category`、标签 `tag`、状态 `status`、库存状态 `stockStatus`、积分区间 `pointsMin,pointsMax` | `id desc`、`pointsPrice asc/desc` |
| 兑换订单 | `/admin/points/exchange-orders` | 兑换单号、用户、商品、积分消耗、状态 | 关键词 `keyword`、兑换单号 `exchangeNo`、用户 `userId`、商品 `goodsId`、状态 `status`、时间 `startTime,endTime` | `createdAt desc` |
| 邀请记录 | `/admin/distribution/invite-records` | 邀请人、被邀人、成单数、贡献金额、邀请时间 | 关键词 `keyword`、邀请人 `inviterId`、被邀人 `inviteeId`、成单数 `orderCountMin,orderCountMax`、贡献金额 `amountMin,amountMax`、邀请时间 `startTime,endTime` | `invitedAt desc`、`contributionAmount desc` |
| 返利记录 | `/admin/distribution/commissions` | 返利用户、金额、状态、结算操作 | 关键词 `keyword`、用户 `userId`、状态 `status`、金额区间 `amountMin,amountMax`、时间 `startTime,endTime` | `createdAt desc`、`amount desc` |
| 动态审核 | `/admin/posts` | 作者、内容、图片、话题、状态、审核 | 关键词 `keyword`、作者 `userId`、话题 `topic`、城市 `city`、状态 `status`、发布时间 `startTime,endTime`、点赞区间 `likeMin,likeMax` | `createdAt desc`、`likeCount desc` |
| 活动管理 | `/admin/activities` | 活动封面、标题、标签、价格、状态、时间 | 关键词 `keyword`、标签 `tag`、状态 `status`、价格区间 `priceMin,priceMax`、活动时间 `startTime,endTime` | `startAt desc`、`price asc/desc` |
| 通知公告 | `/admin/notifications` | 标题、内容、类型、接收用户、已读状态 | 关键词 `keyword`、类型 `type`、读取状态 `readStatus`、用户 `userId`、发送时间 `startTime,endTime` | `createdAt desc` |

### 7.3 管理端关键页面交互

- 订单管理：
  - 支持从待支付、制作中、待出餐、已完成、已取消等状态筛选。
  - 订单详情支持查看取餐码、商品明细、支付信息、备注。
  - 状态更新接口必须记录更新时间，核销取餐码需校验订单状态。
- 存酒管理：
  - 支持新增用户存酒记录，编辑剩余量、到期时间、状态。
  - 取酒核销后更新存酒状态与剩余量，并生成相关订单/流水记录（如业务需要）。
- 提现审核：
  - 审核通过后钱包冻结金额转出并新增流水。
  - 驳回后释放冻结金额并写入驳回原因。
- 动态审核：
  - 审核通过后移动端可见。
  - 驳回需记录原因，删除为逻辑删除或状态置违规。

## 8. 业务流程

### 8.1 点餐下单流程

1. 首页点击自助点餐或底部点餐。
2. 在点餐页按分类浏览商品。
3. 点击加号选择规格并加入购物车。
4. 打开购物车确认商品数量。
5. 点击去结算进入确认订单。
6. 确认门店座位、商品、支付方式和备注。
7. 提交订单并支付。
8. 支付成功进入订单详情展示取餐码。
9. 用户向店员展示取餐码完成取餐。

### 8.2 订单管理流程

1. 我的订单按全部/制作中/待出餐/已完成/已取消筛选。
2. 制作中订单展示排队号码与等待时间。
3. 待出餐订单可查看详情或取餐码。
4. 已完成订单可再来一单或评价赢积分。
5. 取消订单展示退款状态。

### 8.3 存酒取酒流程

1. 我的存酒搜索或筛选存酒。
2. 查看剩余容量、到期时间、存入时间。
3. 点击立即取酒生成取酒申请或取酒订单。
4. 若存酒临期可点击续存申请。
5. 取酒订单进入订单详情展示取酒码。

### 8.4 钱包资金流程

1. 进入我的钱包查看余额。
2. 查看资金明细、提现记录和提现账号。
3. 点击充值选择金额并支付。
4. 点击提现选择账号和金额提交申请。
5. 后台审核提现后更新钱包流水。

### 8.5 积分与兑换流程

1. 我的积分查看可用积分和明细。
2. 从积分入口进入积分商城。
3. 按分类浏览热门兑换商品。
4. 点击立即兑换进入确认页。
5. 扣减积分并生成兑换订单。
6. 签到、消费购物、邀请好友、餐饮打卡增加积分。

### 8.6 分销邀请流程

1. 进入分销中心查看累计收益、邀请人数、待入账收益。
2. 点击立即邀请好友生成分享链接或海报。
3. 好友注册下单后形成邀请记录和贡献金额。
4. 收益满足条件后提现至钱包。

### 8.7 社交广场流程

1. 进入广场动态按附近搭子、今日热门、约个饭筛选。
2. 在推荐/附近/最新频道浏览双列动态。
3. 点击动态进入详情并点赞评论。
4. 点击悬浮加号发布图片、文字、位置和话题。
5. 从作者信息进入个人主页并私信 TA。

### 8.8 资料与会员流程

1. 我的页查看会员等级、余额、积分进度和功能入口。
2. 进入编辑资料修改头像、昵称、性别、年龄、电话、简介、相册和微信二维码。
3. 开关附近好友展示控制社交曝光。
4. 开通超级会员获得权益。

### 8.9 桌面互动流程

1. 从首页互动游戏或点餐页进入桌面互动。
2. 查看桌号、预约状态、基础积分与座位占用。
3. 选择空位入座或查看已入座桌友。
4. 已入座后可取消座位。
5. 座位状态同步到订单确认的当前座位。

### 8.10 后台运营流程

1. 管理员登录后台。
2. 维护门店、桌台、分类、商品、活动和积分商品。
3. 查看并处理订单制作、出餐、取消、退款。
4. 管理存酒、取酒和续存申请。
5. 审核提现、查看钱包流水与分销返利。
6. 审核广场动态和用户资料。

## 9. 数据模型

### 9.1 表设计总览

| 实体 | 建议表名 | 说明 | 主要关联 |
|---|---|---|---|
| User | `travel_user` | 用户/会员资料 | 订单、钱包、积分、分销、动态、座位 |
| Store | `travel_store` | 门店信息 | 桌台、订单、存酒 |
| DiningTable | `travel_dining_table` | 桌台 | 门店、座位、订单 |
| TableSeat | `travel_table_seat` | 座位状态 | 桌台、用户 |
| ProductCategory | `travel_product_category` | 商品分类 | 商品 |
| Product | `travel_product` | 点餐/酒水商品 | 分类、购物车、订单明细 |
| CartItem | `travel_cart_item` | 购物车明细 | 用户、商品 |
| Order | `travel_order` | 订单主表 | 用户、门店、桌台、订单明细 |
| OrderItem | `travel_order_item` | 订单商品明细 | 订单、商品 |
| StoredWine | `travel_stored_wine` | 用户存酒 | 用户、门店 |
| WalletAccount | `travel_wallet_account` | 钱包账户 | 用户、流水 |
| WalletTransaction | `travel_wallet_transaction` | 资金流水 | 用户、关联业务 |
| WithdrawAccount | `travel_withdraw_account` | 提现账号 | 用户 |
| PointRecord | `travel_point_record` | 积分明细 | 用户、关联业务 |
| PointGoods | `travel_point_goods` | 积分商城商品 | 兑换订单 |
| PointExchangeOrder | `travel_point_exchange_order` | 积分兑换订单 | 用户、积分商品 |
| DistributionAccount | `travel_distribution_account` | 分销账户 | 用户、邀请记录 |
| InviteRecord | `travel_invite_record` | 邀请与返利记录 | 邀请人、被邀人 |
| Activity | `travel_activity` | 精选活动/团购套餐 | 首页活动 |
| Post | `travel_post` | 广场动态 | 用户 |
| Notification | `travel_notification` | 通知消息 | 用户 |

### 9.2 字段定义

#### User（用户/会员资料）

| 字段 | 类型建议 | 说明 |
|---|---|---|
| id | bigint PK | 主键 |
| token | varchar(128) | 登录 token 或 token 关联字段 |
| nickname | varchar(64) | 昵称 |
| avatarUrl | varchar(512) | 头像 |
| gender | varchar(16) | 性别 |
| age | int | 年龄 |
| phone | varchar(32) | 手机号 |
| city | varchar(64) | 城市 |
| bio | varchar(512) | 简介 |
| level | varchar(32) | 会员等级 |
| memberId | varchar(64) | 会员编号 |
| points | int | 当前积分 |
| walletBalance | decimal(10,2) | 钱包余额 |
| showNearby | tinyint | 是否附近展示 |
| albumImages | json/text | 个人相册图片 |
| wechatQrUrl | varchar(512) | 微信二维码 |
| createdAt | datetime | 创建时间 |

索引：
- 唯一索引：`uk_user_phone(phone)`。
- 普通索引：`idx_user_created_at(createdAt)`、`idx_user_level(level)`、`idx_user_city(city)`。

#### Store（门店信息）

字段：`id`、`name`、`address`、`phone`、`businessHours`、`latitude`、`longitude`、`status`。

索引：
- `idx_store_status(status)`。
- `idx_store_name(name)`。

#### DiningTable（桌台与座位）

字段：`id`、`storeId`、`name`、`code`、`capacity`、`occupiedCount`、`status`、`updatedAt`。

索引：
- `idx_table_store_status(storeId,status)`。
- 唯一索引：`uk_table_code(code)`。

#### TableSeat（座位状态）

字段：`id`、`tableId`、`seatNo`、`userId`、`avatarUrl`、`nickname`、`status`、`joinedAt`。

索引：
- 唯一索引：`uk_table_seat(tableId,seatNo)`。
- `idx_seat_user(userId)`。

#### ProductCategory（商品分类）

字段：`id`、`name`、`icon`、`sort`、`status`。

索引：
- `idx_category_status_sort(status,sort)`。

#### Product（点餐/酒水商品）

字段：`id`、`categoryId`、`name`、`imageUrl`、`description`、`specs`、`price`、`memberPrice`、`stock`、`sales`、`status`。

索引：
- `idx_product_category_status(categoryId,status)`。
- `idx_product_sales(sales)`。
- `idx_product_name(name)`。

#### CartItem（购物车明细）

字段：`id`、`userId`、`productId`、`productName`、`specText`、`quantity`、`price`、`memberPrice`、`selected`。

索引：
- `idx_cart_user(userId)`。
- `idx_cart_product(productId)`。

#### Order（订单主表）

字段：`id`、`orderNo`、`userId`、`storeId`、`tableId`、`tableName`、`pickupCode`、`status`、`queuePosition`、`estimatedWaitMinutes`、`subtotalAmount`、`packageFee`、`discountAmount`、`payAmount`、`paymentMethod`、`payStatus`、`remark`、`createdAt`、`paidAt`、`completedAt`、`cancelReason`。

索引：
- 唯一索引：`uk_order_no(orderNo)`。
- 普通索引：`idx_order_user_created(userId,createdAt)`、`idx_order_store_status(storeId,status)`、`idx_order_pickup_code(pickupCode)`、`idx_order_pay_status(payStatus)`。

#### OrderItem（订单商品明细）

字段：`id`、`orderId`、`productId`、`productName`、`imageUrl`、`specText`、`quantity`、`unitPrice`、`totalPrice`。

索引：
- `idx_order_item_order(orderId)`。
- `idx_order_item_product(productId)`。

#### StoredWine（用户存酒）

字段：`id`、`userId`、`storeId`、`name`、`imageUrl`、`typeName`、`remainingText`、`remainingAmount`、`storedAt`、`expireAt`、`status`、`canRenew`。

索引：
- `idx_wine_user_status(userId,status)`。
- `idx_wine_store_expire(storeId,expireAt)`。
- `idx_wine_type(typeName)`。

#### WalletAccount（钱包账户）

字段：`id`、`userId`、`balance`、`frozenAmount`、`totalIncome`、`updatedAt`。

索引：
- 唯一索引：`uk_wallet_user(userId)`。

#### WalletTransaction（资金流水）

字段：`id`、`userId`、`type`、`amount`、`direction`、`title`、`status`、`createdAt`、`relatedId`。

索引：
- `idx_wallet_tx_user_created(userId,createdAt)`。
- `idx_wallet_tx_type_status(type,status)`。

#### WithdrawAccount（提现账号）

字段：`id`、`userId`、`accountType`、`accountName`、`accountNo`、`isDefault`。

索引：
- `idx_withdraw_account_user(userId)`。

#### PointRecord（积分明细）

字段：`id`、`userId`、`scene`、`title`、`points`、`direction`、`createdAt`、`relatedId`。

索引：
- `idx_point_user_created(userId,createdAt)`。
- `idx_point_scene(scene)`。

#### PointGoods（积分商城商品）

字段：`id`、`name`、`imageUrl`、`pointsPrice`、`tag`、`category`、`stock`、`status`、`description`。

索引：
- `idx_point_goods_category_status(category,status)`。
- `idx_point_goods_points(pointsPrice)`。

#### PointExchangeOrder（积分兑换订单）

字段：`id`、`exchangeNo`、`userId`、`goodsId`、`pointsCost`、`status`、`receiverInfo`、`createdAt`。

索引：
- 唯一索引：`uk_exchange_no(exchangeNo)`。
- `idx_exchange_user_created(userId,createdAt)`。
- `idx_exchange_goods(goodsId)`。

#### DistributionAccount（分销账户）

字段：`id`、`userId`、`totalIncome`、`pendingIncome`、`inviteCount`、`inviteCode`。

索引：
- 唯一索引：`uk_distribution_user(userId)`。
- 唯一索引：`uk_invite_code(inviteCode)`。

#### InviteRecord（邀请与返利记录）

字段：`id`、`inviterId`、`inviteeId`、`inviteeName`、`inviteeAvatar`、`orderCount`、`contributionAmount`、`invitedAt`。

索引：
- `idx_invite_inviter(inviterId)`。
- `idx_invite_invitee(inviteeId)`。
- `idx_invite_time(invitedAt)`。

#### Activity（精选活动/团购套餐）

字段：`id`、`title`、`coverUrl`、`tag`、`description`、`price`、`originalPrice`、`status`、`startAt`、`endAt`。

索引：
- `idx_activity_status_time(status,startAt,endAt)`。
- `idx_activity_tag(tag)`。

#### Post（广场动态）

字段：`id`、`userId`、`authorName`、`authorAvatar`、`content`、`imageUrls`、`topic`、`city`、`distanceText`、`ageText`、`likeCount`、`commentCount`、`status`、`createdAt`。

索引：
- `idx_post_user(userId)`。
- `idx_post_status_created(status,createdAt)`。
- `idx_post_topic(topic)`。
- `idx_post_city(city)`。

#### Notification（通知消息）

字段：`id`、`userId`、`title`、`content`、`type`、`readStatus`、`createdAt`。

索引：
- `idx_notification_user_read(userId,readStatus)`。
- `idx_notification_type_created(type,createdAt)`。

### 9.3 关键枚举

| 枚举 | 值 |
|---|---|
| 用户状态 | `enabled`、`disabled` |
| 门店状态 | `open`、`closed`、`disabled` |
| 桌台状态 | `available`、`reserved`、`occupied`、`disabled` |
| 座位状态 | `empty`、`occupied`、`locked` |
| 商品状态 | `on_sale`、`off_sale`、`sold_out` |
| 订单状态 | `pending_pay`、`making`、`ready`、`completed`、`cancelled` |
| 支付状态 | `unpaid`、`paid`、`refunded`、`failed` |
| 支付方式 | `wechat`、`wallet` |
| 存酒状态 | `available`、`pickup_pending`、`finished`、`expired` |
| 钱包流水类型 | `recharge`、`withdraw`、`order_pay`、`refund`、`distribution` |
| 流水方向 | `income`、`expense` |
| 积分方向 | `income`、`expense` |
| 兑换订单状态 | `pending`、`processing`、`completed`、`cancelled` |
| 动态状态 | `pending`、`approved`、`rejected`、`deleted` |
| 活动状态 | `draft`、`published`、`offline` |

## 10. 接口清单

### 10.1 App 接口（BasePath：`/sqx_fast/app`）

| 方法 | 路径 | 鉴权 | 描述 | 主要入参/查询参数 |
|---|---|---|---|---|
| POST | `/sqx_fast/app/user/login` | none | 用户端登录，校验账号密码并返回 token | `account/phone`、`password` |
| POST | `/sqx_fast/app/user/register` | none | 用户端注册 | `phone`、`nickname`、`password`、`inviteCode?` |
| GET | `/sqx_fast/app/home/summary` | token | 首页会员、积分、活动和快捷入口汇总 | 无 |
| GET | `/sqx_fast/app/user/profile` | token | 获取当前用户资料 | 无 |
| PUT | `/sqx_fast/app/user/profile` | token | 保存编辑资料 | User 可编辑字段 |
| POST | `/sqx_fast/app/upload/image` | token | 上传头像、相册、二维码或动态图片 | `file`、`scene` |
| GET | `/sqx_fast/app/products/categories` | token | 商品分类列表 | `status?` |
| GET | `/sqx_fast/app/products` | token | 商品列表，支持分类、关键词、门店筛选 | `categoryId`、`keyword`、`storeId`、`page`、`pageSize` |
| GET | `/sqx_fast/app/products/{id}` | token | 商品详情与规格 | `id` |
| GET | `/sqx_fast/app/cart` | token | 购物车列表与合计 | 无 |
| POST | `/sqx_fast/app/cart/items` | token | 加入购物车 | `productId`、`specText`、`quantity` |
| PUT | `/sqx_fast/app/cart/items/{id}` | token | 修改购物车数量/选中状态 | `quantity`、`selected` |
| DELETE | `/sqx_fast/app/cart/items/{id}` | token | 删除购物车商品 | `id` |
| DELETE | `/sqx_fast/app/cart` | token | 清空购物车 | 无 |
| POST | `/sqx_fast/app/orders/preview` | token | 确认订单预览，计算金额 | `cartItemIds`、`storeId`、`tableId`、`paymentMethod` |
| POST | `/sqx_fast/app/orders` | token | 创建订单 | `cartItemIds`、`storeId`、`tableId`、`remark`、`paymentMethod` |
| POST | `/sqx_fast/app/orders/{id}/pay` | token | 订单支付 | `paymentMethod`、`payPassword?` |
| GET | `/sqx_fast/app/orders` | token | 我的订单列表，按状态筛选 | `status`、`page`、`pageSize` |
| GET | `/sqx_fast/app/orders/{id}` | token | 订单详情与取餐码 | `id` |
| POST | `/sqx_fast/app/orders/{id}/cancel` | token | 取消订单 | `cancelReason` |
| POST | `/sqx_fast/app/orders/{id}/repeat` | token | 再来一单 | `id` |
| POST | `/sqx_fast/app/orders/{id}/review` | token | 评价订单并奖励积分 | `rating`、`content` |
| GET | `/sqx_fast/app/stored-wines` | token | 我的存酒列表，支持搜索筛选 | `keyword`、`typeName`、`status`、`remainingMin`、`remainingMax`、`expireStart`、`expireEnd`、`page`、`pageSize` |
| POST | `/sqx_fast/app/stored-wines/{id}/pickup` | token | 申请立即取酒 | `id`、`pickupAmount?` |
| POST | `/sqx_fast/app/stored-wines/{id}/renew` | token | 续存申请 | `id`、`renewDays` |
| GET | `/sqx_fast/app/wallet` | token | 钱包余额与资产汇总 | 无 |
| GET | `/sqx_fast/app/wallet/transactions` | token | 资金明细 | `type`、`direction`、`status`、`startTime`、`endTime`、`page`、`pageSize` |
| POST | `/sqx_fast/app/wallet/recharge` | token | 充值下单 | `amount`、`paymentMethod` |
| POST | `/sqx_fast/app/wallet/withdraw` | token | 提交提现申请 | `amount`、`withdrawAccountId` |
| GET | `/sqx_fast/app/wallet/withdraw-records` | token | 提现记录 | `status`、`page`、`pageSize` |
| GET | `/sqx_fast/app/wallet/withdraw-accounts` | token | 提现账号列表 | 无 |
| POST | `/sqx_fast/app/wallet/withdraw-accounts` | token | 新增提现账号 | `accountType`、`accountName`、`accountNo`、`isDefault` |
| GET | `/sqx_fast/app/points` | token | 积分汇总 | 无 |
| GET | `/sqx_fast/app/points/records` | token | 积分明细 | `scene`、`direction`、`startTime`、`endTime`、`page`、`pageSize` |
| GET | `/sqx_fast/app/points/mall/goods` | token | 积分商城商品列表 | `category`、`keyword`、`tag`、`page`、`pageSize` |
| GET | `/sqx_fast/app/points/mall/goods/{id}` | token | 积分商品详情 | `id` |
| POST | `/sqx_fast/app/points/mall/exchange` | token | 积分兑换 | `goodsId`、`receiverInfo` |
| GET | `/sqx_fast/app/points/mall/orders` | token | 我的兑换记录 | `status`、`page`、`pageSize` |
| POST | `/sqx_fast/app/points/sign-in` | token | 签到赚积分 | 无 |
| GET | `/sqx_fast/app/distribution/summary` | token | 分销中心汇总 | 无 |
| GET | `/sqx_fast/app/distribution/invitees` | token | 邀请榜单和邀请记录 | `tab`、`page`、`pageSize` |
| POST | `/sqx_fast/app/distribution/share` | token | 生成邀请链接或海报 | `scene` |
| POST | `/sqx_fast/app/distribution/withdraw` | token | 分销收益提现到钱包 | `amount` |
| GET | `/sqx_fast/app/users/{id}/homepage` | token | 查看用户个人主页 | `id` |
| POST | `/sqx_fast/app/users/{id}/message` | token | 私信用户 | `content` |
| GET | `/sqx_fast/app/posts` | token | 广场动态列表，支持频道、距离、热门筛选 | `scene`、`channel`、`keyword`、`city`、`page`、`pageSize` |
| POST | `/sqx_fast/app/posts` | token | 发布动态 | `content`、`imageUrls`、`topic`、`city` |
| GET | `/sqx_fast/app/posts/{id}` | token | 动态详情 | `id` |
| POST | `/sqx_fast/app/posts/{id}/like` | token | 点赞或取消点赞 | `id` |
| GET | `/sqx_fast/app/activities` | token | 精选活动列表 | `tag`、`status`、`page`、`pageSize` |
| GET | `/sqx_fast/app/activities/{id}` | token | 活动详情 | `id` |
| GET | `/sqx_fast/app/tables/{tableId}/seats` | token | 桌台座位状态 | `tableId` |
| POST | `/sqx_fast/app/tables/{tableId}/seats/{seatNo}/join` | token | 选择座位入座 | `tableId`、`seatNo` |
| POST | `/sqx_fast/app/tables/{tableId}/seats/cancel` | token | 取消当前入座 | `tableId` |
| GET | `/sqx_fast/app/reservations` | token | 我的预约列表 | `status`、`page`、`pageSize` |
| POST | `/sqx_fast/app/reservations` | token | 创建在线预约 | `storeId`、`tableId?`、`reservationTime`、`peopleCount` |
| GET | `/sqx_fast/app/notifications` | token | 通知列表 | `type`、`readStatus`、`page`、`pageSize` |
| GET | `/sqx_fast/app/help` | token | 帮助中心 | `keyword`、`category` |

### 10.2 Admin 接口（BasePath：`/sqx_fast/admin`）

| 方法 | 路径 | 鉴权 | 描述 | 主要入参/查询参数 |
|---|---|---|---|---|
| POST | `/sqx_fast/admin/user/login` | none | 管理端登录，校验账号密码并返回 token，不接入菜单权限或 RBAC | `account`、`password` |
| GET | `/sqx_fast/admin/dashboard/summary` | token | 后台经营数据概览 | `storeId`、`startDate`、`endDate` |
| GET | `/sqx_fast/admin/users` | token | 用户列表查询 | `keyword`、`phone`、`level`、`status`、`city`、`startTime`、`endTime`、`sortField`、`sortOrder`、`page`、`pageSize` |
| GET | `/sqx_fast/admin/users/{id}` | token | 用户详情 | `id` |
| PUT | `/sqx_fast/admin/users/{id}/status` | token | 启用/禁用用户 | `status` |
| GET | `/sqx_fast/admin/stores` | token | 门店列表 | `keyword`、`status`、`businessHours`、`addressKeyword`、`sortField`、`sortOrder`、`page`、`pageSize` |
| POST | `/sqx_fast/admin/stores` | token | 新增门店 | Store 字段 |
| PUT | `/sqx_fast/admin/stores/{id}` | token | 编辑门店 | Store 字段 |
| DELETE | `/sqx_fast/admin/stores/{id}` | token | 删除门店 | `id` |
| GET | `/sqx_fast/admin/tables` | token | 桌台列表与座位状态 | `keyword`、`storeId`、`status`、`capacityMin`、`capacityMax`、`startTime`、`endTime`、`sortField`、`sortOrder`、`page`、`pageSize` |
| POST | `/sqx_fast/admin/tables` | token | 新增桌台 | DiningTable 字段 |
| PUT | `/sqx_fast/admin/tables/{id}` | token | 编辑桌台 | DiningTable 字段 |
| PUT | `/sqx_fast/admin/tables/{id}/status` | token | 更新桌台预约/空闲/停用状态 | `status` |
| GET | `/sqx_fast/admin/products/categories` | token | 商品分类管理列表 | `keyword`、`status`、`sortMin`、`sortMax`、`sortField`、`sortOrder`、`page`、`pageSize` |
| POST | `/sqx_fast/admin/products/categories` | token | 新增商品分类 | ProductCategory 字段 |
| PUT | `/sqx_fast/admin/products/categories/{id}` | token | 编辑商品分类 | ProductCategory 字段 |
| DELETE | `/sqx_fast/admin/products/categories/{id}` | token | 删除商品分类 | `id` |
| GET | `/sqx_fast/admin/products` | token | 商品管理列表 | `keyword`、`categoryId`、`status`、`stockStatus`、`priceMin`、`priceMax`、`salesMin`、`salesMax`、`startTime`、`endTime`、`sortField`、`sortOrder`、`page`、`pageSize` |
| POST | `/sqx_fast/admin/products` | token | 新增商品 | Product 字段 |
| PUT | `/sqx_fast/admin/products/{id}` | token | 编辑商品 | Product 字段 |
| PUT | `/sqx_fast/admin/products/{id}/status` | token | 上下架商品 | `status` |
| DELETE | `/sqx_fast/admin/products/{id}` | token | 删除商品 | `id` |
| GET | `/sqx_fast/admin/orders` | token | 订单列表，支持状态、门店、时间筛选 | `keyword`、`orderNo`、`userId`、`storeId`、`status`、`payStatus`、`paymentMethod`、`startTime`、`endTime`、`amountMin`、`amountMax`、`sortField`、`sortOrder`、`page`、`pageSize` |
| GET | `/sqx_fast/admin/orders/{id}` | token | 订单详情 | `id` |
| PUT | `/sqx_fast/admin/orders/{id}/status` | token | 更新制作中、待出餐、已完成、已取消状态 | `status`、`reason?` |
| POST | `/sqx_fast/admin/orders/{id}/verify-pickup` | token | 核销取餐码 | `pickupCode` |
| GET | `/sqx_fast/admin/stored-wines` | token | 存酒记录管理 | `keyword`、`userId`、`storeId`、`typeName`、`status`、`canRenew`、`remainingMin`、`remainingMax`、`expireStart`、`expireEnd`、`sortField`、`sortOrder`、`page`、`pageSize` |
| POST | `/sqx_fast/admin/stored-wines` | token | 新增用户存酒记录 | StoredWine 字段 |
| PUT | `/sqx_fast/admin/stored-wines/{id}` | token | 编辑存酒剩余量、到期时间和状态 | StoredWine 字段 |
| POST | `/sqx_fast/admin/stored-wines/{id}/verify-pickup` | token | 核销取酒申请 | `pickupCode?`、`pickupAmount?` |
| GET | `/sqx_fast/admin/wallet/transactions` | token | 钱包流水查询 | `keyword`、`userId`、`type`、`direction`、`status`、`amountMin`、`amountMax`、`startTime`、`endTime`、`sortField`、`sortOrder`、`page`、`pageSize` |
| GET | `/sqx_fast/admin/wallet/withdraws` | token | 提现申请列表 | `keyword`、`userId`、`status`、`accountType`、`amountMin`、`amountMax`、`startTime`、`endTime`、`sortField`、`sortOrder`、`page`、`pageSize` |
| POST | `/sqx_fast/admin/wallet/withdraws/{id}/approve` | token | 审核通过提现 | `remark?` |
| POST | `/sqx_fast/admin/wallet/withdraws/{id}/reject` | token | 驳回提现 | `rejectReason` |
| GET | `/sqx_fast/admin/points/records` | token | 积分流水查询 | `keyword`、`userId`、`scene`、`direction`、`pointsMin`、`pointsMax`、`startTime`、`endTime`、`sortField`、`sortOrder`、`page`、`pageSize` |
| GET | `/sqx_fast/admin/points/goods` | token | 积分商品列表 | `keyword`、`category`、`tag`、`status`、`stockStatus`、`pointsMin`、`pointsMax`、`sortField`、`sortOrder`、`page`、`pageSize` |
| POST | `/sqx_fast/admin/points/goods` | token | 新增积分商品 | PointGoods 字段 |
| PUT | `/sqx_fast/admin/points/goods/{id}` | token | 编辑积分商品 | PointGoods 字段 |
| PUT | `/sqx_fast/admin/points/goods/{id}/status` | token | 上下架积分商品 | `status` |
| GET | `/sqx_fast/admin/points/exchange-orders` | token | 积分兑换订单列表 | `keyword`、`exchangeNo`、`userId`、`goodsId`、`status`、`startTime`、`endTime`、`sortField`、`sortOrder`、`page`、`pageSize` |
| PUT | `/sqx_fast/admin/points/exchange-orders/{id}/status` | token | 处理兑换订单 | `status`、`remark?` |
| GET | `/sqx_fast/admin/distribution/invite-records` | token | 分销邀请记录 | `keyword`、`inviterId`、`inviteeId`、`orderCountMin`、`orderCountMax`、`amountMin`、`amountMax`、`startTime`、`endTime`、`sortField`、`sortOrder`、`page`、`pageSize` |
| GET | `/sqx_fast/admin/distribution/commissions` | token | 分销返利记录 | `keyword`、`userId`、`status`、`amountMin`、`amountMax`、`startTime`、`endTime`、`sortField`、`sortOrder`、`page`、`pageSize` |
| PUT | `/sqx_fast/admin/distribution/commissions/{id}/settle` | token | 结算返利 | `remark?` |
| GET | `/sqx_fast/admin/posts` | token | 广场动态审核列表 | `keyword`、`userId`、`topic`、`city`、`status`、`startTime`、`endTime`、`likeMin`、`likeMax`、`sortField`、`sortOrder`、`page`、`pageSize` |
| PUT | `/sqx_fast/admin/posts/{id}/audit` | token | 审核通过或驳回动态 | `status`、`reason?` |
| DELETE | `/sqx_fast/admin/posts/{id}` | token | 删除违规动态 | `id` |
| GET | `/sqx_fast/admin/activities` | token | 活动列表管理 | `keyword`、`tag`、`status`、`priceMin`、`priceMax`、`startTime`、`endTime`、`sortField`、`sortOrder`、`page`、`pageSize` |
| POST | `/sqx_fast/admin/activities` | token | 新增活动 | Activity 字段 |
| PUT | `/sqx_fast/admin/activities/{id}` | token | 编辑活动 | Activity 字段 |
| PUT | `/sqx_fast/admin/activities/{id}/status` | token | 发布/下线活动 | `status` |
| GET | `/sqx_fast/admin/notifications` | token | 通知公告列表 | `keyword`、`type`、`readStatus`、`userId`、`startTime`、`endTime`、`sortField`、`sortOrder`、`page`、`pageSize` |
| POST | `/sqx_fast/admin/notifications` | token | 发送通知公告 | `title`、`content`、`type`、`userIds?` |

## 11. 接口协议与返回结构

### 11.1 通用分页返回

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "list": [],
    "total": 0,
    "page": 1,
    "pageSize": 10
  }
}
```

### 11.2 通用错误码

| code | 说明 |
|---|---|
| 0 | 成功 |
| 400 | 参数错误 |
| 401 | 未登录或 token 无效 |
| 403 | 无权访问业务资源 |
| 404 | 数据不存在 |
| 409 | 状态冲突，例如库存不足、重复入座 |
| 500 | 系统异常 |

### 11.3 token 规则

- Header：`token: <登录返回token>`。
- App 与 Admin token 作用域分离，不能互通。
- 登录、注册、帮助公开内容如需匿名访问可不传 token，但本 PRD 默认除登录/注册外均需 token。

## 12. 验收标准

### 12.1 架构验收

- 服务端目录必须为 `e2e-autoprd-1781188447795-server`，端口 9294，Context Path `/sqx_fast`。
- 管理端目录必须为 `e2e-autoprd-1781188447795-admin`，端口 5173，路由归属 `/admin/**`。
- 移动端目录必须为 `e2e-autoprd-1781188447795-app`，H5 端口 8080，路由归属 `/app/**`。
- 不得从零重写工程，不得擅自修改依赖版本和目录结构。

### 12.2 登录与鉴权验收

- `POST /sqx_fast/admin/user/login` 可独立完成管理员登录并返回 token。
- `POST /sqx_fast/app/user/login` 可独立完成用户登录并返回 token。
- App/Admin 登录接口互不混用。
- 登录/注册接口无菜单权限、Shiro `@RequiresPermissions`、RBAC 校验。
- 除登录/注册外，接口未传 token 时返回 `401`。

### 12.3 移动端 UI 验收

- 所有移动端页面必须使用本 PRD 第 5 节 designTokens：
  - 主色为 `#6F35E8`，主渐变为 `linear-gradient(135deg,#6F35E8 0%,#9B63FF 100%)`。
  - 页面背景、卡片背景、文字色、边框、阴影、圆角与字体规格需与 tokens 一致。
- 识图页面 layout/components 必须 1:1 还原：
  - 首页：欢迎 Banner、双核心入口、六宫格、活动横滑、悬浮按钮、底部 tabbar。
  - 点餐：左侧分类、右侧商品卡、底部购物车结算栏。
  - 订单详情：等待出餐状态、A086 风格大号取餐码、商品卡、订单信息卡、底部栏。
  - 我的存酒：搜索、筛选、存酒卡、立即取酒、续存申请。
  - 钱包/积分/分销/我的/资料/广场/桌台互动页面按本 PRD 组件说明完成。
- 推断补全页面必须可达：
  - 登录/注册、商品详情/规格、购物车弹层、支付结果、存酒筛选、提现/充值、积分兑换确认、动态详情/发布动态。
- uni-app + uview-plus 组件习惯验收：
  - 表单使用 `u-form`/`u-input`，上传使用 `u-upload`，弹层使用 `u-popup`，列表筛选 tab 使用 `u-tabs`，导航使用 `u-navbar`。
  - H5 端 8080 可正常访问并完成核心流程。

### 12.4 管理端 UI 验收

- 所有管理端列表页必须具备多条件筛选、重置、分页、表格加载状态和空状态。
- 每个列表页至少实现本 PRD 第 7.2 节定义的关键词、状态、时间范围、分类/门店、排序等筛选参数。
- 新增/编辑/详情/审核/状态更新页面或弹窗必须覆盖：
  - 门店、桌台、分类、商品、订单、存酒、提现、积分商品、兑换订单、分销、动态、活动、通知。
- Element Plus 表单需包含必要校验，例如必填、手机号格式、金额非负、库存非负、时间范围合法。

### 12.5 后端接口验收

- App 与 Admin 接口按第 10 节完整实现，BasePath 分别为 `/sqx_fast/app` 与 `/sqx_fast/admin`。
- 查询接口支持分页参数 `page`、`pageSize`，返回统一分页结构。
- 管理端列表接口支持对应筛选与排序参数。
- 订单创建需校验商品状态、库存、购物车归属、支付方式与金额计算。
- 钱包提现需校验余额、提现账号、冻结金额与流水记录。
- 积分兑换需校验积分余额、商品库存、兑换状态，并生成积分流水。
- 桌台入座需校验座位是否为空，防止重复入座。
- 动态发布默认进入待审核或按产品规则可见，管理端可审核通过/驳回。

### 12.6 数据验收

- 数据库名称为 `travel`。
- 表结构覆盖第 9 节所有实体、字段、索引和关联关系。
- 金额字段使用 decimal，时间字段使用 datetime，图片数组/规格可使用 JSON 或 text。
- 订单号、兑换单号、桌台编码、手机号、邀请码等关键唯一字段必须建唯一索引。
- 删除类操作如涉及业务数据，优先状态变更或逻辑删除，避免破坏历史订单与流水。

### 12.7 业务流程验收

- 点餐下单流程可从首页进入点餐页，加入购物车，确认订单，支付成功，查看取餐码。
- 订单管理流程可按状态筛选，查看制作中、待出餐、已完成、已取消订单，并支持再来一单。
- 存酒流程可搜索筛选，立即取酒，续存申请，并由后台核销。
- 钱包流程可充值、提现、查看流水，后台可审核提现。
- 积分流程可查看明细、进入商城、兑换商品、生成兑换订单。
- 分销流程可查看收益、邀请记录、生成分享、提现至钱包。
- 社交流程可浏览、发布、点赞、进入个人主页并私信。
- 桌面互动流程可展示座位状态、选择空位、取消入座并同步当前座位。

### 12.8 测试与交付验收

- 服务端需包含实体、Mapper、Service、Controller、统一响应体、工具类、枚举常量。
- 服务端需编写接口单元测试，覆盖正常流程、参数校验、异常场景。
- 管理端需内置模拟数据或可连接正式接口，页面不因空数据报错。
- 移动端需完成 H5 运行自测，核心页面布局符合识图稿。
- 最终交付包含：
  - 完整源码。
  - 单元测试报告。
  - 功能模拟测试结果。
  - 部署启动步骤。

## 13. 部署启动说明（供开发交付补充）

### 13.1 服务端

```bash
cd e2e-autoprd-1781188447795-server
mvn clean test
mvn spring-boot:run
```

- 访问 Context：`http://127.0.0.1:9294/sqx_fast`
- 数据库：`travel`

### 13.2 管理端

```bash
cd e2e-autoprd-1781188447795-admin
npm install
npm run dev -- --host 0.0.0.0 --port 5173
```

- 访问：`http://127.0.0.1:5173/admin`

### 13.3 移动端 H5

```bash
cd e2e-autoprd-1781188447795-app
npm install
npm run dev:h5 -- --host 0.0.0.0 --port 8080
```

- 访问：`http://127.0.0.1:8080/app`

## 14. 开发 Agent 实现提示

- 先设计并落地数据库表、字段、索引、关联关系。
- 再按 Admin/App 接口拆分后端 Controller，不混用路径与鉴权。
- 后端完成后编写单元测试和接口自测。
- 管理端基于接口协议开发列表、筛选、分页、新增/编辑/详情/审核页面。
- 移动端基于识图稿与 designTokens 进行 1:1 还原，所有补全子页面必须列入 pages 配置并可跳转。
- 三端接口入参、出参、状态码和字典枚举保持一致。

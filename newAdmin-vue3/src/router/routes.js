import Layout from '@/layout/index.vue'

export const routes = [
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '数据看板' }
      },
      {
        path: 'product/list',
        name: 'ProductList',
        component: () => import('@/views/product/list.vue'),
        meta: { title: '酒水商品' }
      },
      {
        path: 'product/edit',
        name: 'ProductEdit',
        component: () => import('@/views/product/edit.vue'),
        meta: { title: '商品编辑', hidden: true }
      },
      {
        path: 'package/list',
        name: 'PackageList',
        component: () => import('@/views/package/list.vue'),
        meta: { title: '套餐管理' }
      },
      {
        path: 'package/edit',
        name: 'PackageEdit',
        component: () => import('@/views/package/edit.vue'),
        meta: { title: '套餐编辑', hidden: true }
      },
      {
        path: 'order/list',
        name: 'OrderList',
        component: () => import('@/views/order/list.vue'),
        meta: { title: '订单管理' }
      },
      {
        path: 'order/detail',
        name: 'OrderDetail',
        component: () => import('@/views/order/detail.vue'),
        meta: { title: '订单详情', hidden: true }
      },
      {
        path: 'booking/list',
        name: 'BookingList',
        component: () => import('@/views/booking/list.vue'),
        meta: { title: '预约管理' }
      },
      {
        path: 'booking/detail',
        name: 'BookingDetail',
        component: () => import('@/views/booking/detail.vue'),
        meta: { title: '预约详情', hidden: true }
      },
      {
        path: 'activity/list',
        name: 'ActivityList',
        component: () => import('@/views/activity/list.vue'),
        meta: { title: '活动管理' }
      },
      {
        path: 'activity/edit',
        name: 'ActivityEdit',
        component: () => import('@/views/activity/edit.vue'),
        meta: { title: '活动编辑', hidden: true }
      },
      {
        path: 'user/list',
        name: 'UserList',
        component: () => import('@/views/user/list.vue'),
        meta: { title: '会员管理' }
      },
      {
        path: 'user/detail',
        name: 'UserDetail',
        component: () => import('@/views/user/detail.vue'),
        meta: { title: '会员详情', hidden: true }
      },
      {
        path: 'coupon/list',
        name: 'CouponList',
        component: () => import('@/views/coupon/list.vue'),
        meta: { title: '团购券管理' }
      },
      {
        path: 'coupon/edit',
        name: 'CouponEdit',
        component: () => import('@/views/coupon/edit.vue'),
        meta: { title: '团购券编辑', hidden: true }
      },
      {
        path: 'game/list',
        name: 'GameList',
        component: () => import('@/views/game/list.vue'),
        meta: { title: '互动游戏' }
      },
      {
        path: 'game/edit',
        name: 'GameEdit',
        component: () => import('@/views/game/edit.vue'),
        meta: { title: '游戏编辑', hidden: true }
      },
      {
        path: 'rank/config',
        name: 'RankConfig',
        component: () => import('@/views/rank/config.vue'),
        meta: { title: '排行榜配置' }
      },
      {
        path: 'notification/list',
        name: 'NotificationList',
        component: () => import('@/views/notification/list.vue'),
        meta: { title: '通知管理' }
      },
      {
        path: 'notification/edit',
        name: 'NotificationEdit',
        component: () => import('@/views/notification/edit.vue'),
        meta: { title: '通知编辑', hidden: true }
      },
      {
        path: 'table/list',
        name: 'TableList',
        component: () => import('@/views/table/list.vue'),
        meta: { title: '桌位管理' }
      }
    ]
  }
]

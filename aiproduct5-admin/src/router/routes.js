import Layout from '@/layout/index.vue'

export const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '商家登录', public: true }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/seller/dashboard',
    children: [
      {
        path: 'seller/dashboard',
        name: 'SellerDashboard',
        component: () => import('@/views/seller/dashboard/index.vue'),
        meta: { title: '工作台', breadcrumb: '工作台' }
      },
      {
        path: 'seller/activity/list',
        name: 'ActivityList',
        component: () => import('@/views/seller/activity/list.vue'),
        meta: { title: '活动管理', breadcrumb: '活动列表' }
      },
      {
        path: 'seller/activity/create',
        name: 'ActivityCreate',
        component: () => import('@/views/seller/activity/create.vue'),
        meta: { title: '创建活动', breadcrumb: '创建活动', hidden: true }
      },
      {
        path: 'seller/activity/edit',
        name: 'ActivityEdit',
        component: () => import('@/views/seller/activity/create.vue'),
        meta: { title: '编辑活动', breadcrumb: '编辑活动', hidden: true }
      },
      {
        path: 'seller/activity/detail',
        name: 'ActivityDetail',
        component: () => import('@/views/seller/activity/detail.vue'),
        meta: { title: '活动详情', breadcrumb: '活动详情', hidden: true }
      },
      {
        path: 'seller/verify/list',
        name: 'VerifyList',
        component: () => import('@/views/seller/verify/list.vue'),
        meta: { title: '核销管理', breadcrumb: '核销审核' }
      },
      {
        path: 'seller/ai/history',
        name: 'AiHistory',
        component: () => import('@/views/seller/ai/history.vue'),
        meta: { title: 'AI 生成记录', breadcrumb: 'AI 记录' }
      }
    ]
  }
]

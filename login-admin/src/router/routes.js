import Layout from '@/layout/index.vue'

export const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '管理员登录', public: true }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/account/list',
    children: [
      {
        path: 'account/list',
        name: 'AccountList',
        component: () => import('@/views/account/list.vue'),
        meta: { title: '账号管理' }
      },
      {
        path: 'account/form',
        name: 'AccountForm',
        component: () => import('@/views/account/form.vue'),
        meta: { title: '账号表单', hidden: true }
      }
    ]
  }
]

import { createRouter, createWebHashHistory } from 'vue-router'
import { routes } from './routes'
import { isLoggedIn } from '@/utils/auth'

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 路由守卫：未登录跳转登录页，已登录访问登录页则跳转首页
router.beforeEach((to, from, next) => {
  if (to.meta.public) {
    if (to.path === '/login' && isLoggedIn()) {
      next('/home')
      return
    }
    next()
    return
  }
  if (!isLoggedIn()) {
    next('/login')
    return
  }
  next()
})

export default router

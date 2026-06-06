import { createRouter, createWebHashHistory } from 'vue-router'
import { routes } from './routes'
import { isLoggedIn } from '@/utils/auth'

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

/** 路由守卫：未登录跳转登录页，已登录禁止访问登录页 */
router.beforeEach((to, from, next) => {
  const loggedIn = isLoggedIn()
  if (to.meta.public) {
    if (loggedIn && to.path === '/login') {
      next('/account/list')
      return
    }
    next()
    return
  }
  if (!loggedIn) {
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }
  next()
})

export default router

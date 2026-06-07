import { createRouter, createWebHashHistory } from 'vue-router'
import { routes } from './routes'
import { isLoggedIn } from '@/utils/auth'

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  if (to.meta.public) {
    // 登录页始终可访问，避免本地残留 token 导致无法重新登录
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

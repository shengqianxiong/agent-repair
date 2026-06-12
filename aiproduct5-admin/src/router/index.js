import { createRouter, createWebHashHistory } from 'vue-router'
import { routes } from './routes'
import { isLoggedIn } from '@/utils/auth'

const router = createRouter({
  history: createWebHashHistory('/admin/'),
  routes
})

router.beforeEach((to, from, next) => {
  if (to.meta.public) {
    if (to.path === '/login' && isLoggedIn()) {
      next('/seller/dashboard')
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

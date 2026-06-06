import { createRouter, createWebHashHistory } from 'vue-router'
import { routes } from './routes'
import { isLoggedIn } from '@/utils/auth'

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  if (to.meta.public) {
    if (to.path === '/login' && isLoggedIn()) {
      next('/account/list')
      return
    }
    next()
    return
  }

  if (!isLoggedIn()) {
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }

  next()
})

export default router

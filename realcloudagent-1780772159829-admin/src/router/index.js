import { createRouter, createWebHistory } from 'vue-router'
import routes from './routes'
import { isLoggedIn } from '@/utils/auth'

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - RealCloudAgent` : 'RealCloudAgent'

  if (to.meta.public) {
    if (to.path === '/login' && isLoggedIn()) {
      next('/home')
    } else {
      next()
    }
    return
  }

  if (!isLoggedIn()) {
    next('/login')
    return
  }

  next()
})

export default router

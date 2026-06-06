import { createSSRApp } from 'vue'
import uviewPlus from 'uview-plus'
import App from './App.vue'
import { ensureLogin } from './utils/auth'

export function createApp() {
  const app = createSSRApp(App)
  app.use(uviewPlus)
  ensureLogin()
  return { app }
}

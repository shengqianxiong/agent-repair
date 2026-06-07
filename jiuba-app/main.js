import { createSSRApp } from 'vue'
import App from './App.vue'
import uviewPlus from 'uview-plus'
import AppNavbar from './components/app-navbar/app-navbar.vue'
import AppTabbar from './components/app-tabbar/app-tabbar.vue'
import EmptyState from './components/empty-state/empty-state.vue'
import LoadingOverlay from './components/loading-overlay/loading-overlay.vue'

export function createApp() {
  const app = createSSRApp(App)
  app.use(uviewPlus)
  app.component('AppNavbar', AppNavbar)
  app.component('AppTabbar', AppTabbar)
  app.component('EmptyState', EmptyState)
  app.component('LoadingOverlay', LoadingOverlay)
  return { app }
}

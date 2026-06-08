import { createSSRApp } from 'vue'
import App from './App.vue'
import uviewPlus from 'uview-plus'
import EmptyState from './components/empty-state/empty-state.vue'
import LoadingOverlay from './components/loading-overlay/loading-overlay.vue'

export function createApp() {
  const app = createSSRApp(App)
  app.use(uviewPlus)
  app.component('EmptyState', EmptyState)
  app.component('LoadingOverlay', LoadingOverlay)
  return { app }
}

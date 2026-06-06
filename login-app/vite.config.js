import { defineConfig } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'

export default defineConfig({
  plugins: [uni()],
  server: {
    port: 8081,
    proxy: {
      '/sqx_fast': {
        target: 'http://127.0.0.1:9295',
        changeOrigin: true
      }
    }
  }
})

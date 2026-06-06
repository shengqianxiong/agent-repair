import { defineConfig } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'

export default defineConfig({
  plugins: [uni()],
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: '@import "uview-plus/theme.scss";'
      }
    }
  },
  server: {
    port: 8080,
    proxy: {
      '/sqx_fast': {
        target: 'http://127.0.0.1:9294',
        changeOrigin: true
      }
    }
  }
})

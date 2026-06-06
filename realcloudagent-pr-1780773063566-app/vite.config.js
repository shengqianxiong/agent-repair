import { defineConfig } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'
import path from 'path'

export default defineConfig({
  plugins: [uni()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname)
    }
  },
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: '@import "@/uni.scss";'
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

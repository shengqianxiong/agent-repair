import path from 'path'
import { defineConfig } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'

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
        additionalData: '@import "@/uni.scss"; @import "uview-plus/theme.scss"; @import "uview-plus/libs/css/mixin.scss";'
      }
    }
  },
  server: {
    port: 8080,
    host: '0.0.0.0'
  }
})

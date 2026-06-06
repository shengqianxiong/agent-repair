import path from 'path'
import { defineConfig } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'

export default defineConfig({
  plugins: [uni()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, '.')
    }
  },
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: '@import "@/uni.scss";',
        silenceDeprecations: ['legacy-js-api', 'import']
      }
    }
  },
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

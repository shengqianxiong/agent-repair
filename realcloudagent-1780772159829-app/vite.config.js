import { defineConfig } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'
import path from 'path'

export default defineConfig({
  plugins: [uni()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: '@import "uview-plus/theme.scss";',
        silenceDeprecations: ['legacy-js-api', 'color-functions', 'import']
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

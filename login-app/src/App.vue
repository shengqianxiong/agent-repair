<script setup>
import { onLaunch, onShow } from '@dcloudio/uni-app'
import { isLoggedIn } from '@/utils/auth.js'

/** 白名单页面（无需登录） */
const PUBLIC_PAGES = ['/pages/login/index']

function checkAuth() {
  const pages = getCurrentPages()
  if (!pages.length) return
  const currentPage = pages[pages.length - 1]
  const route = '/' + (currentPage.route || '')
  const loggedIn = isLoggedIn()

  if (!loggedIn && !PUBLIC_PAGES.includes(route)) {
    uni.reLaunch({ url: '/pages/login/index' })
    return
  }
  if (loggedIn && route === '/pages/login/index') {
    uni.reLaunch({ url: '/pages/home/index' })
  }
}

onLaunch(() => {
  checkAuth()
})

onShow(() => {
  checkAuth()
})
</script>

<style lang="scss">
@import 'uview-plus/index.scss';
@import '@/styles/page-layout.scss';

page {
  background-color: #f5f7fa;
  color: #303133;
  font-size: 28rpx;
}
</style>

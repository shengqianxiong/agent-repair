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
    uni.reLaunch({ url: '/pages/index/index' })
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
  height: 100%;
  overflow: hidden;
  background-color: #0f0f1a;
  color: #e8e8f0;
  font-size: 28rpx;
}

/* #ifdef H5 */
html,
body,
#app {
  height: 100%;
  overflow: hidden;
}
/* #endif */
</style>

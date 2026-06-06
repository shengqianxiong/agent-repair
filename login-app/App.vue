<script setup>
import { onLaunch, onShow } from '@dcloudio/uni-app'
import { isLoggedIn } from '@/utils/auth.js'

/**
 * 根据登录态将用户引导至正确页面
 */
function redirectByAuth() {
  const pages = getCurrentPages()
  if (!pages.length) {
    return
  }

  const currentRoute = pages[pages.length - 1].route || ''
  const loggedIn = isLoggedIn()

  if (!loggedIn && currentRoute !== 'pages/login/index') {
    uni.reLaunch({ url: '/pages/login/index' })
    return
  }

  if (loggedIn && currentRoute === 'pages/login/index') {
    uni.reLaunch({ url: '/pages/home/index' })
  }
}

onLaunch(() => {
  console.log('login-app 启动')
})

onShow(() => {
  redirectByAuth()
})
</script>

<style lang="scss">
@import 'uview-plus/index.scss';

page {
  background-color: #f3f4f6;
  color: #1f2937;
  font-size: 28rpx;
}
</style>

<script setup>
import { onLaunch } from '@dcloudio/uni-app'
import { isLoggedIn } from '@/utils/auth.js'

const LOGIN_PATH = '/pages/login/login'
const INDEX_PATH = '/pages/index/index'

onLaunch(() => {
  const pages = getCurrentPages()
  const currentRoute = pages.length ? `/${pages[pages.length - 1].route}` : LOGIN_PATH

  if (!isLoggedIn() && currentRoute !== LOGIN_PATH) {
    uni.reLaunch({ url: LOGIN_PATH })
  }
})
</script>

<style lang="scss">
@import 'uview-plus/index.scss';

page {
  background-color: var(--color-background, #F8F9FB);
  color: var(--color-text-primary, #333333);
  font-family: PingFang SC, -apple-system, sans-serif;
  font-size: 28rpx;
}
</style>

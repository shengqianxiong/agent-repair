<script setup>
import { onLaunch } from '@dcloudio/uni-app'
import { isLoggedIn } from '@/utils/auth.js'

const LOGIN_PAGE = '/pages/login/login'
const WHITE_LIST = [LOGIN_PAGE]

/** 全局路由鉴权拦截 */
function setupAuthGuard() {
  ;['navigateTo', 'redirectTo', 'reLaunch', 'switchTab'].forEach((api) => {
    uni.addInterceptor(api, {
      invoke(args) {
        const path = (args.url || '').split('?')[0]
        if (!WHITE_LIST.includes(path) && !isLoggedIn()) {
          uni.reLaunch({ url: LOGIN_PAGE })
          return false
        }
        return true
      }
    })
  })
}

onLaunch(() => {
  setupAuthGuard()
})
</script>

<style lang="scss">
@import 'uview-plus/index.scss';

page {
  background-color: var(--color-background);
  color: var(--color-text-primary);
  font-family: PingFang SC, -apple-system, sans-serif;
  font-size: var(--font-size-sm);
}
</style>

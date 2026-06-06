<script setup>
import { onLaunch } from '@dcloudio/uni-app'
import { isLoggedIn, clearAuth } from '@/utils/auth.js'

onLaunch(() => {
  // 启动时校验本地 Token 是否过期，过期则清理登录态
  if (!isLoggedIn()) {
    clearAuth()
  }
})
</script>

<style lang="scss">
@import 'uview-plus/index.scss';

page {
  background-color: #f5f7fa;
  color: #303133;
  font-size: 28rpx;
}
</style>

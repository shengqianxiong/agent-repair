<script setup>
import { onLaunch } from '@dcloudio/uni-app'
import { isLoggedIn } from '@/utils/auth.js'

onLaunch(() => {
  // 启动时清理已过期的本地会话
  isLoggedIn()
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

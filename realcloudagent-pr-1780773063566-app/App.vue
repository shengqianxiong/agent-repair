<script setup>
import { onLaunch } from '@dcloudio/uni-app'
import { isLoggedIn } from '@/utils/auth.js'

onLaunch(() => {
  if (!isLoggedIn()) {
    uni.reLaunch({ url: '/pages/login/index' })
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

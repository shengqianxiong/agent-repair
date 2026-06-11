<script setup>
import { onLaunch } from '@dcloudio/uni-app'

onLaunch(() => {
  const token = uni.getStorageSync('app_token')
  if (!token) {
    uni.setStorageSync('app_token', 'demo-token')
  }
})
</script>

<style lang="scss">
@import 'uview-plus/index.scss';
@import '@/styles/common.scss';

page {
  background: var(--color-page-bg);
  color: var(--color-text-primary);
  font-size: 28rpx;
  font-family: -apple-system, BlinkMacSystemFont, 'PingFang SC', 'Microsoft YaHei', sans-serif;
}
</style>

<script setup>
import { onLaunch } from '@dcloudio/uni-app'
import { ensureLogin } from '@/utils/auth'

onLaunch(() => {
  ensureLogin()
})
</script>

<style lang="scss">
@import 'uview-plus/index.scss';

page {
  background-color: #0f0f1a;
  color: #e8e8f0;
  font-size: 28rpx;
}
</style>

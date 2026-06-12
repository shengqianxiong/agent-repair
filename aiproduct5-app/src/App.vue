<script setup>
import { onLaunch } from '@dcloudio/uni-app'
import { ensureVisitorId } from '@/utils/auth.js'

onLaunch(() => {
  ensureVisitorId()
})
</script>

<style lang="scss">
@import 'uview-plus/index.scss';

page {
  --dp-primary: #1463ff;
  --dp-primary-light: #edf4ff;
  --dp-success: #25c06d;
  --dp-warning: #ff9f18;
  --dp-error: #ff5a5f;
  --dp-text-main: #20263a;
  --dp-text-regular: #626b82;
  --dp-text-muted: #98a2b3;
  --dp-border: #edf0f5;
  --dp-page-bg: #f6f8fc;
  --dp-card-bg: #ffffff;
  background-color: var(--dp-page-bg);
  color: var(--dp-text-main);
  font-size: 28rpx;
}

.dp-page {
  min-height: 100vh;
  box-sizing: border-box;
  background: var(--dp-page-bg);
}

.dp-safe-bottom {
  padding-bottom: calc(32rpx + env(safe-area-inset-bottom));
}

/* #ifdef H5 */
@media screen and (min-width: 480px) {
  body {
    background: #eef2f8;
  }

  uni-page-body {
    max-width: 430px;
    min-height: 100vh;
    margin: 0 auto;
    overflow: hidden;
  }

  .uni-tabbar,
  .uni-page-head {
    left: 50% !important;
    width: 430px !important;
    max-width: 430px;
    transform: translateX(-50%);
  }
}
/* #endif */
</style>

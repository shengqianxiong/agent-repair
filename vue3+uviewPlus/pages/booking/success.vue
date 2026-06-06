<template>
  <view class="success-page">
    <view class="success-content">
      <u-icon name="checkmark-circle-fill" color="#00b894" size="80" />
      <text class="title">预约成功</text>
      <text class="subtitle">我们已收到您的预约，请等待门店确认</text>

      <view class="bar-card summary">
        <u-cell title="预约日期" :value="date" />
        <u-cell title="预约时段" :value="time" />
        <u-cell title="用餐人数" :value="count + ' 人'" />
      </view>

      <view class="actions">
        <u-button type="primary" text="查看详情" @click="goDetail" />
        <u-button type="info" plain text="返回首页" @click="goHome" />
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'

const bookingId = ref('')
const date = ref('')
const time = ref('')
const count = ref('')

function goDetail() {
  if (bookingId.value) {
    uni.redirectTo({ url: `/pages/booking/detail?id=${bookingId.value}` })
  }
}

function goHome() {
  uni.reLaunch({ url: '/pages/index/index' })
}

onLoad((opts) => {
  bookingId.value = opts.id
  date.value = opts.date
  time.value = opts.time
  count.value = opts.count
})
</script>

<style lang="scss" scoped>
.success-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #1a1a2e 0%, #f5f5f5 40%);
  padding: 120rpx 24rpx;
}

.success-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.title {
  font-size: 40rpx;
  font-weight: bold;
  color: #333;
  margin-top: 32rpx;
}

.subtitle {
  font-size: 26rpx;
  color: #999;
  margin: 16rpx 0 40rpx;
}

.summary {
  width: 100%;
}

.actions {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 20rpx;
  margin-top: 40rpx;
}
</style>

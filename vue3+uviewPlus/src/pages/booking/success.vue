<template>
  <view class="page">
    <view class="success-card">
      <u-icon name="checkmark-circle-fill" size="80" color="#10b981" />
      <text class="title">预约成功</text>
      <text class="desc">我们已收到您的预约，请等待门店确认</text>
    </view>

    <view class="actions">
      <u-button type="primary" text="查看详情" @click="goDetail" />
      <u-button text="返回首页" @click="goHome" />
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'

const bookingId = ref('')

onLoad((options) => {
  bookingId.value = options.id || ''
})

function goDetail() {
  if (bookingId.value) {
    uni.redirectTo({ url: `/pages/booking/detail?id=${bookingId.value}` })
  }
}

function goHome() {
  uni.switchTab({ url: '/pages/index/index' })
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #0f0f1a;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48rpx;
}
.success-card {
  text-align: center;
  margin-bottom: 64rpx;
}
.title {
  display: block;
  font-size: 40rpx;
  font-weight: 600;
  color: #fff;
  margin: 24rpx 0 12rpx;
}
.desc {
  font-size: 28rpx;
  color: #a0a0b8;
}
.actions {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}
</style>

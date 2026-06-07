<template>
  <view v-if="show" class="loading-overlay">
    <u-loading-icon mode="circle" color="#7B61FF" size="36"></u-loading-icon>
    <text class="loading-text">{{ text }}</text>
  </view>
</template>

<script setup>
defineProps({
  show: { type: Boolean, default: false },
  text: { type: String, default: '加载中...' }
})
</script>

<style lang="scss" scoped>
.loading-overlay {
  position: fixed;
  inset: 0;
  z-index: 9999;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.7);
  gap: 16rpx;
}
.loading-text {
  font-size: 28rpx;
  color: #666666;
}
</style>

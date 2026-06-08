<template>
  <view class="page page-fixed">
    <u-navbar title="预约成功" :autoBack="false" bgColor="#1a1a2e" titleStyle="color:#fff"></u-navbar>
    <scroll-view scroll-y class="page-body" :show-scrollbar="false">
    <view class="success-content">
      <u-icon name="checkmark-circle-fill" color="#10b981" size="80"></u-icon>
      <text class="title">预约提交成功</text>
      <text class="subtitle">请等待门店确认</text>

      <view class="summary">
        <view class="row"><text>日期</text><text>{{ date }}</text></view>
        <view class="row"><text>时段</text><text>{{ time }}</text></view>
        <view class="row"><text>人数</text><text>{{ count }} 人</text></view>
      </view>

      <view class="btns">
        <u-button type="info" text="返回首页" @click="goHome"></u-button>
        <u-button v-if="bookingId" type="primary" text="查看详情" @click="goDetail"></u-button>
      </view>
    </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const bookingId = ref('')
const date = ref('')
const time = ref('')
const count = ref('')

function goHome() {
  uni.switchTab({ url: '/pages/index/index' })
}

function goDetail() {
  uni.redirectTo({ url: `/pages/booking/detail?id=${bookingId.value}` })
}

onMounted(() => {
  const pages = getCurrentPages()
  const opts = pages[pages.length - 1].options || {}
  bookingId.value = opts.id || ''
  date.value = opts.date || ''
  time.value = opts.time || ''
  count.value = opts.count || ''
})
</script>

<style lang="scss" scoped>
.page { background: #0f0f1a; }
.success-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80rpx 48rpx;
}
.title { font-size: 40rpx; font-weight: 700; color: #fff; margin-top: 32rpx; }
.subtitle { color: #a0a0b8; margin-top: 12rpx; }
.summary {
  width: 100%;
  background: #1a1a2e;
  border-radius: 16rpx;
  padding: 32rpx;
  margin-top: 48rpx;
}
.row {
  display: flex;
  justify-content: space-between;
  padding: 16rpx 0;
  color: #e8e8f0;
}
.btns {
  width: 100%;
  margin-top: 48rpx;
  display: flex;
  gap: 20rpx;
}
</style>

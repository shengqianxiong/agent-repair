<template>
  <view class="page" v-if="activity">
    <u-navbar :title="activity.title" :autoBack="true" bgColor="#1a1a2e" titleStyle="color:#fff"></u-navbar>

    <u-image :src="activity.image || defaultImg" width="100%" height="400rpx"></u-image>

    <view class="content">
      <view class="title-row">
        <u-tag v-if="activity.tag" :text="activity.tag" type="error" size="mini"></u-tag>
        <text class="title">{{ activity.title }}</text>
      </view>

      <view class="countdown-row" v-if="endTime">
        <text>距结束</text>
        <u-count-down :time="countdownMs" format="DD:HH:mm:ss"></u-count-down>
      </view>

      <view class="price-row">
        <text class="price">¥{{ formatPrice(activity.price) }}</text>
        <text v-if="activity.originalPrice" class="original">¥{{ formatPrice(activity.originalPrice) }}</text>
      </view>

      <text class="desc">{{ activity.description || '限时特惠活动' }}</text>
    </view>

    <view class="footer">
      <u-button type="primary" text="立即抢购" :loading="buying" @click="buy"></u-button>
    </view>
  </view>
  <u-loading-page v-else loading></u-loading-page>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getActivityDetail, buyActivity } from '@/api/index.js'
import { formatPrice } from '@/utils/common.js'

const defaultImg = 'https://cdn.uviewui.com/uview/swiper/2.jpg'
const activity = ref(null)
const buying = ref(false)

const endTime = computed(() => activity.value?.endTime)

const countdownMs = computed(() => {
  if (!endTime.value) return 0
  const end = new Date(endTime.value).getTime()
  return Math.max(0, end - Date.now())
})

async function loadDetail(id) {
  try {
    activity.value = await getActivityDetail(id)
  } catch (e) {
    uni.navigateBack()
  }
}

async function buy() {
  buying.value = true
  try {
    const order = await buyActivity(activity.value.id)
    uni.redirectTo({
      url: `/pages/payment/index?orderId=${order.id || order.orderId}&amount=${order.payAmount || activity.value.price}`
    })
  } catch (e) {
  } finally {
    buying.value = false
  }
}

onMounted(() => {
  const pages = getCurrentPages()
  const id = pages[pages.length - 1].options?.id
  if (id) loadDetail(id)
})
</script>

<style lang="scss" scoped>
.page { min-height: 100vh; background: #0f0f1a; padding-bottom: 140rpx; }
.content { padding: 32rpx; }
.title-row { display: flex; align-items: center; gap: 12rpx; margin-bottom: 16rpx; }
.title { font-size: 36rpx; font-weight: 700; color: #fff; }
.countdown-row {
  display: flex;
  align-items: center;
  gap: 16rpx;
  color: #ef4444;
  margin-bottom: 16rpx;
}
.price-row { display: flex; align-items: baseline; gap: 16rpx; margin-bottom: 24rpx; }
.price { font-size: 48rpx; color: #f59e0b; font-weight: 700; }
.original { color: #6b6b80; text-decoration: line-through; }
.desc { color: #a0a0b8; line-height: 1.6; }
.footer {
  position: fixed;
  left: 32rpx;
  right: 32rpx;
  bottom: calc(48rpx + env(safe-area-inset-bottom));
}
</style>

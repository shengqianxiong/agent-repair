<template>
  <view class="page" v-if="activity">
    <u-image :src="activity.image" width="100%" height="400rpx" />

    <view class="info-card">
      <view class="title-row">
        <text class="title">{{ activity.title }}</text>
        <u-tag v-if="activity.tag" :text="activity.tag" size="mini" type="error" />
      </view>
      <text class="desc">{{ activity.description }}</text>

      <view class="price-row">
        <text class="price">¥{{ formatPrice(activity.price) }}</text>
        <text v-if="activity.originalPrice" class="original">¥{{ formatPrice(activity.originalPrice) }}</text>
      </view>

      <view class="countdown" v-if="activity.endTime">
        <text>距结束</text>
        <u-count-down :time="countdownMs" format="DD:HH:mm:ss" />
      </view>
    </view>

    <view class="bottom-bar">
      <u-button type="error" text="立即抢购" :loading="buying" @click="doBuy" />
    </view>
  </view>
  <u-loading-page :loading="loading" />
</template>

<script setup>
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getActivityDetail, buyActivity } from '@/api/activity'
import { formatPrice } from '@/utils/navigate'

const activity = ref(null)
const loading = ref(true)
const buying = ref(false)
let activityId = null

const countdownMs = computed(() => {
  if (!activity.value?.endTime) return 0
  const end = new Date(activity.value.endTime).getTime()
  return Math.max(0, end - Date.now())
})

onLoad((options) => {
  activityId = options.id
  loadDetail()
})

async function loadDetail() {
  loading.value = true
  try {
    activity.value = await getActivityDetail(activityId)
  } catch (e) {
    uni.showToast({ title: e.msg || '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

async function doBuy() {
  buying.value = true
  try {
    const data = await buyActivity(activityId)
    const orderId = data?.id || data?.orderId
    uni.redirectTo({
      url: `/pages/payment/index?orderId=${orderId}&amount=${activity.value.price}`
    })
  } catch (e) {
    uni.showToast({ title: e.msg || '抢购失败', icon: 'none' })
  } finally {
    buying.value = false
  }
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #0f0f1a;
  padding-bottom: 140rpx;
}
.info-card {
  padding: 24rpx;
  background: #1a1a2e;
  margin: 16rpx;
  border-radius: 12rpx;
}
.title-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 12rpx;
}
.title {
  font-size: 36rpx;
  font-weight: 600;
  color: #fff;
}
.desc {
  font-size: 28rpx;
  color: #a0a0b8;
  line-height: 1.6;
}
.price-row {
  margin: 20rpx 0;
  display: flex;
  align-items: baseline;
  gap: 12rpx;
}
.price {
  font-size: 44rpx;
  color: #ef4444;
  font-weight: 700;
}
.original {
  font-size: 26rpx;
  color: #6b6b80;
  text-decoration: line-through;
}
.countdown {
  display: flex;
  align-items: center;
  gap: 12rpx;
  color: #f59e0b;
  font-size: 26rpx;
}
.bottom-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 20rpx 24rpx;
  background: #1a1a2e;
}
</style>

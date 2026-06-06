<template>
  <view class="bar-page" v-if="activity">
    <u-navbar :title="activity.title" autoBack bgColor="transparent" titleColor="#fff" leftIconColor="#fff" />

    <u-image :src="activity.image" width="100%" height="400rpx" />

    <view class="bar-card">
      <view class="title-row">
        <u-tag v-if="activity.tag" :text="activity.tag" type="error" size="mini" />
        <text class="title">{{ activity.title }}</text>
      </view>
      <view class="price-row">
        <text class="price-text">¥{{ formatPrice(activity.price) }}</text>
        <text v-if="activity.originalPrice" class="original">¥{{ formatPrice(activity.originalPrice) }}</text>
      </view>
      <view class="countdown" v-if="activity.endTime">
        <text>距结束 </text>
        <u-count-down :time="countdownTime" format="HH:mm:ss" />
      </view>
      <text class="desc">{{ activity.description }}</text>
    </view>

    <view class="bottom-bar safe-bottom">
      <u-button type="error" text="立即抢购" :loading="buying" @click="doBuy" />
    </view>
  </view>
  <u-loading-page v-else loading />
</template>

<script setup>
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getActivityDetail, buyActivity } from '@/api/marketing'
import { formatPrice } from '@/utils/request'

const activity = ref(null)
const activityId = ref('')
const buying = ref(false)

const countdownTime = computed(() => {
  if (!activity.value?.endTime) return 0
  const end = new Date(activity.value.endTime).getTime()
  return Math.max(0, end - Date.now())
})

async function loadDetail() {
  activity.value = await getActivityDetail(activityId.value)
}

async function doBuy() {
  buying.value = true
  try {
    const order = await buyActivity(activityId.value)
    uni.redirectTo({
      url: `/pages/payment/index?orderId=${order.id || order.orderId}&amount=${order.payAmount || activity.value.price}`
    })
  } finally {
    buying.value = false
  }
}

onLoad((opts) => {
  activityId.value = opts.id
  loadDetail()
})
</script>

<style lang="scss" scoped>
.title-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 16rpx;
}

.title {
  font-size: 34rpx;
  font-weight: bold;
}

.price-row {
  display: flex;
  align-items: baseline;
  gap: 12rpx;
  margin-bottom: 16rpx;
}

.original {
  color: #999;
  font-size: 24rpx;
  text-decoration: line-through;
}

.countdown {
  display: flex;
  align-items: center;
  gap: 8rpx;
  margin-bottom: 16rpx;
  color: #d63031;
  font-size: 26rpx;
}

.desc {
  font-size: 26rpx;
  color: #666;
  line-height: 1.6;
}

.bottom-bar {
  position: fixed;
  left: 24rpx;
  right: 24rpx;
  bottom: 40rpx;
}
</style>

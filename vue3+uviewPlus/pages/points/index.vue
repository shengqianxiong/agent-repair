<template>
  <view class="bar-page">
    <view class="points-header bar-gradient-header">
      <text class="label">当前积分</text>
      <text class="balance">{{ balance }}</text>
      <u-button size="small" type="warning" text="积分兑换" @click="goRedeem" />
    </view>

    <view class="bar-card">
      <view class="section-title">积分流水</view>
      <view v-if="!records.length" class="empty-wrap">
        <u-empty mode="list" text="暂无流水" />
      </view>
      <view v-for="item in records" :key="item.id" class="record-item">
        <view class="record-left">
          <text class="source">{{ item.source || item.type }}</text>
          <text class="time">{{ formatDateTime(item.createTime) }}</text>
        </view>
        <text class="points" :class="{ minus: item.points < 0 }">
          {{ item.points > 0 ? '+' : '' }}{{ item.points }}
        </text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow, onPullDownRefresh } from '@dcloudio/uni-app'
import { getPointsBalance, getPointsRecords } from '@/api/marketing'
import { formatDateTime } from '@/utils/request'

const balance = ref(0)
const records = ref([])

async function loadData() {
  const [bal, rec] = await Promise.allSettled([
    getPointsBalance(),
    getPointsRecords({ page: 1, pageSize: 30 })
  ])
  if (bal.status === 'fulfilled') {
    balance.value = bal.value?.balance ?? bal.value ?? 0
  }
  if (rec.status === 'fulfilled') {
    records.value = rec.value?.list || rec.value || []
  }
}

function goRedeem() {
  uni.navigateTo({ url: '/pages/points/redeem' })
}

onShow(() => loadData())
onPullDownRefresh(async () => { await loadData(); uni.stopPullDownRefresh() })
</script>

<style lang="scss" scoped>
.points-header {
  margin: 24rpx;
  border-radius: 16rpx;
  padding: 40rpx;
  text-align: center;

  .label {
    color: rgba(255, 255, 255, 0.8);
    font-size: 26rpx;
    display: block;
  }

  .balance {
    color: #fff;
    font-size: 72rpx;
    font-weight: bold;
    display: block;
    margin: 16rpx 0 24rpx;
  }
}

.section-title {
  font-weight: bold;
  margin-bottom: 16rpx;
}

.record-item {
  display: flex;
  justify-content: space-between;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.record-left {
  .source {
    display: block;
    font-size: 28rpx;
  }

  .time {
    font-size: 22rpx;
    color: #999;
  }
}

.points {
  font-size: 32rpx;
  font-weight: bold;
  color: #00b894;

  &.minus {
    color: #d63031;
  }
}
</style>

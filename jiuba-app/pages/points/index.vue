<template>
  <view class="page">
    <LoadingOverlay :show="loading" />
    <AppNavbar title="我的积分" right-icon="bell" bg-color="#F8F9FB" />

    <scroll-view scroll-y class="content">
      <view class="summary-card">
        <view class="summary-left">
          <text class="summary-label">当前可用积分</text>
          <text class="summary-value">{{ balance }}</text>
          <view class="mall-btn" @click="goMall">积分商城</view>
        </view>
        <view class="robot-icon">
          <u-icon name="star-fill" color="#7B61FF" size="60"></u-icon>
        </view>
      </view>

      <view class="list-header">
        <text class="list-title">积分明细</text>
        <u-icon name="hourglass" color="#999" size="18" @click="showToast('筛选')"></u-icon>
      </view>

      <view v-if="records.length === 0 && !loading" class="empty-wrap"><EmptyState text="暂无积分记录" /></view>
      <view v-for="item in records" :key="item.id" class="record-card">
        <view class="record-icon" :class="item.type">
          <u-icon :name="item.icon || 'integral'" color="#7B61FF" size="20"></u-icon>
        </view>
        <view class="record-info">
          <text class="record-title">{{ item.title }}</text>
          <text class="record-time">{{ item.time }}</text>
        </view>
        <text class="record-points" :class="{ spend: item.points < 0 }">{{ item.points > 0 ? '+' : '' }}{{ item.points }}</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getPointsBalance, getPointsRecords } from '@/api/index.js'
import { showToast } from '@/utils/common.js'

const loading = ref(false)
const balance = ref(0)
const records = ref([])

async function loadData() {
  loading.value = true
  try {
    const [bal, rec] = await Promise.all([getPointsBalance(), getPointsRecords()])
    balance.value = bal?.balance ?? 0
    records.value = rec?.list || []
  } finally { loading.value = false }
}

function goMall() { uni.navigateTo({ url: '/pages/points/mall' }) }

onMounted(loadData)
</script>

<style lang="scss" scoped>
.page { min-height: 100vh; background: #F8F9FB; }
.content { padding: 24rpx 32rpx; }
.summary-card {
  display: flex; background: #FFF; border-radius: 48rpx; padding: 40rpx; margin-bottom: 32rpx; box-shadow: var(--shadow-card-purple);
  .summary-left { flex: 1; }
  .summary-label { font-size: 28rpx; color: #666; display: block; }
  .summary-value { font-size: 64rpx; font-weight: 700; color: #7B61FF; display: block; margin: 12rpx 0 24rpx; }
  .mall-btn { display: inline-block; background: #7B61FF; color: #FFF; padding: 16rpx 40rpx; border-radius: 200rpx; font-size: 28rpx; }
  .robot-icon { width: 160rpx; display: flex; align-items: center; justify-content: center; }
}
.list-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20rpx; }
.list-title { font-size: 32rpx; font-weight: 700; color: #1A1A1A; }
.record-card {
  display: flex; align-items: center; background: #FFF; border-radius: 24rpx; padding: 28rpx; margin-bottom: 16rpx; box-shadow: var(--shadow-card);
  .record-icon { width: 72rpx; height: 72rpx; border-radius: 50%; display: flex; align-items: center; justify-content: center; margin-right: 20rpx;
    &.earn { background: #F0EDFF; }
    &.spend { background: #FFEDED; }
  }
  .record-info { flex: 1; }
  .record-title { font-size: 28rpx; color: #333; display: block; }
  .record-time { font-size: 24rpx; color: #999; margin-top: 6rpx; display: block; }
  .record-points { font-size: 32rpx; font-weight: 600; color: #7B61FF;
    &.spend { color: #E64340; }
  }
}
</style>

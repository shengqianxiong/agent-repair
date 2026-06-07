<template>
  <view class="page">
    <LoadingOverlay :show="loading" />
    <AppNavbar title="分销中心" right-icon="question-circle" bg-color="transparent" />

    <scroll-view scroll-y class="content">
      <view class="earnings-card">
        <text class="earnings-label">累计收益(元)</text>
        <text class="earnings-value">¥{{ formatMoney(info.totalEarnings) }}</text>
        <view class="withdraw-btn" @click="showToast('提现至钱包')">提现至钱包 ></view>
      </view>

      <view class="stats-grid">
        <view class="stat-card">
          <view class="stat-icon"><u-icon name="account-fill" color="#7B61FF" size="22"></u-icon></view>
          <text class="stat-value">{{ info.inviteCount }}</text>
          <text class="stat-label">邀请人数</text>
        </view>
        <view class="stat-card">
          <view class="stat-icon"><u-icon name="rmb-circle" color="#7B61FF" size="22"></u-icon></view>
          <text class="stat-value">¥{{ formatMoney(info.pendingEarnings) }}</text>
          <text class="stat-label">待入账收益</text>
        </view>
      </view>

      <view class="promo-banner">
        <text class="promo-title">邀请好友，乐享佣金</text>
        <text class="promo-desc">每邀请一位好友消费即可获得佣金</text>
        <view class="invite-btn" @click="showToast('立即邀请')">立即邀请好友</view>
      </view>

      <view class="leaderboard-section">
        <view class="lb-header">
          <text class="lb-title">邀请榜单</text>
          <text class="lb-more" @click="showToast('查看全部')">查看全部</text>
        </view>
        <view class="lb-tabs">
          <text class="lb-tab active">近期活跃</text>
          <text class="lb-tab">收益贡献</text>
        </view>
        <view v-for="user in leaderboard" :key="user.id" class="lb-item">
          <image class="lb-avatar" :src="user.avatar" />
          <view class="lb-info">
            <text class="lb-name">{{ user.nickname }}</text>
            <text class="lb-date">{{ user.date }}</text>
          </view>
          <text class="lb-amount">+¥{{ user.amount }}</text>
        </view>
        <view class="load-more" @click="showToast('加载更多')">加载更多</view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getDistributionInfo, getLeaderboard } from '@/api/index.js'
import { formatMoney, showToast } from '@/utils/common.js'

const loading = ref(false)
const info = ref({})
const leaderboard = ref([])

async function loadData() {
  loading.value = true
  try {
    const [dist, lb] = await Promise.all([getDistributionInfo(), getLeaderboard()])
    info.value = dist || {}
    leaderboard.value = lb?.list || dist?.leaderboard || []
  } finally { loading.value = false }
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.page { min-height: 100vh; background: #F8F9FB; }
.content { padding: 24rpx 32rpx; }
.earnings-card {
  background: #FFF; border-radius: 48rpx; padding: 40rpx; text-align: center; margin-bottom: 24rpx; box-shadow: var(--shadow-card);
  .earnings-label { font-size: 28rpx; color: #666; display: block; }
  .earnings-value { font-size: 56rpx; font-weight: 700; color: #7B61FF; display: block; margin: 16rpx 0 24rpx; }
  .withdraw-btn { display: inline-block; background: #EBE4FF; color: #7B61FF; padding: 12rpx 32rpx; border-radius: 200rpx; font-size: 26rpx; }
}
.stats-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 24rpx; margin-bottom: 24rpx; }
.stat-card {
  background: #FFF; border-radius: 32rpx; padding: 32rpx; text-align: center; box-shadow: var(--shadow-card);
  .stat-icon { width: 72rpx; height: 72rpx; border-radius: 50%; background: #F0EDFF; display: flex; align-items: center; justify-content: center; margin: 0 auto 16rpx; }
  .stat-value { font-size: 36rpx; font-weight: 700; color: #1A1A1A; display: block; }
  .stat-label { font-size: 24rpx; color: #999; margin-top: 8rpx; display: block; }
}
.promo-banner {
  background: #9C7CFE; border-radius: 40rpx; padding: 40rpx; color: #FFF; margin-bottom: 32rpx; text-align: center;
  .promo-title { font-size: 36rpx; font-weight: 700; display: block; }
  .promo-desc { font-size: 26rpx; opacity: 0.9; margin: 12rpx 0 24rpx; display: block; }
  .invite-btn { display: inline-block; background: #FFF; color: #7B61FF; padding: 16rpx 48rpx; border-radius: 200rpx; font-size: 28rpx; font-weight: 600; }
}
.leaderboard-section {
  background: #FFF; border-radius: 32rpx; padding: 32rpx; box-shadow: var(--shadow-card);
  .lb-header { display: flex; justify-content: space-between; margin-bottom: 20rpx; }
  .lb-title { font-size: 32rpx; font-weight: 700; }
  .lb-more { font-size: 26rpx; color: #7B61FF; }
  .lb-tabs { display: flex; gap: 32rpx; margin-bottom: 24rpx; border-bottom: 1rpx solid #F0F0F0; padding-bottom: 16rpx; }
  .lb-tab { font-size: 28rpx; color: #999;
    &.active { color: #7B61FF; font-weight: 600; border-bottom: 4rpx solid #7B61FF; padding-bottom: 12rpx; }
  }
  .lb-item { display: flex; align-items: center; padding: 20rpx 0; border-bottom: 1rpx solid #F0F0F0; }
  .lb-avatar { width: 72rpx; height: 72rpx; border-radius: 50%; margin-right: 16rpx; }
  .lb-info { flex: 1; }
  .lb-name { font-size: 28rpx; display: block; }
  .lb-date { font-size: 24rpx; color: #999; }
  .lb-amount { font-size: 30rpx; font-weight: 600; color: #7B61FF; }
  .load-more { text-align: center; padding: 24rpx; font-size: 26rpx; color: #7B61FF; }
}
</style>

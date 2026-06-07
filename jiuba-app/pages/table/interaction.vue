<template>
  <view class="page">
    <LoadingOverlay :show="loading" />
    <view class="navbar">
      <view class="nav-left"><u-icon name="list" color="#7B61FF" size="22"></u-icon></view>
      <text class="nav-title">桌面互动</text>
      <view class="nav-right"><u-icon name="bell" color="#7B61FF" size="20"></u-icon></view>
    </view>

    <scroll-view scroll-y class="content">
      <view class="table-header">
        <text class="table-name">{{ table.tableName }}</text>
        <view class="status-dot">
          <view class="dot"></view>
          <text>{{ table.statusText }}</text>
        </view>
        <text class="update-time">更新于 {{ table.updateTime }}</text>
      </view>

      <view class="stats-grid">
        <view class="stat-card">
          <text class="stat-label">基础积分</text>
          <text class="stat-value">{{ table.basePoints }}/{{ table.maxPoints }}</text>
        </view>
        <view class="stat-card">
          <text class="stat-label">座位状态</text>
          <text class="stat-value">{{ table.occupiedSeats }}/{{ table.totalSeats }}</text>
        </view>
      </view>

      <view class="interaction-area">
        <view class="game-title">{{ table.gameName }}</view>
        <view class="seats-ring">
          <view
            v-for="seat in table.seats"
            :key="seat.id"
            class="seat-card"
            :class="{ occupied: seat.occupied, 'is-me': seat.isMe }"
            :style="getSeatStyle(seat.id)"
            @click="selectSeat(seat)"
          >
            <template v-if="seat.occupied">
              <image class="seat-avatar" :src="seat.avatar" />
              <text class="seat-name">{{ seat.nickname }}</text>
            </template>
            <template v-else>
              <u-icon name="account" color="#CCC" size="20"></u-icon>
              <text class="seat-id">{{ seat.id }}</text>
            </template>
          </view>
        </view>
      </view>
    </scroll-view>

    <view v-if="table.mySeat" class="user-status-card">
      <image class="my-avatar" :src="myAvatar" />
      <text class="status-text">我已入座{{ table.mySeat }}</text>
      <view class="cancel-btn" @click="leaveMySeat">取消</view>
    </view>

    <AppTabbar current="menu" />
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getTableInteraction, leaveSeat } from '@/api/index.js'
import { showToast, showLoading, hideLoading } from '@/utils/common.js'

const loading = ref(false)
const table = ref({ seats: [] })
const myAvatar = ref('https://cdn.uviewui.com/uview/album/1.jpg')

const seatPositions = {
  A1: { top: '5%', left: '50%' },
  A2: { top: '20%', left: '80%' },
  A3: { top: '50%', left: '90%' },
  A4: { top: '75%', left: '70%' },
  A5: { top: '85%', left: '40%' },
  A6: { top: '75%', left: '10%' },
  A7: { top: '50%', left: '0%' },
  A8: { top: '20%', left: '15%' }
}

function getSeatStyle(id) {
  const pos = seatPositions[id] || {}
  return { top: pos.top, left: pos.left, transform: 'translate(-50%, -50%)' }
}

async function loadData() {
  loading.value = true
  try { table.value = await getTableInteraction() || {} } finally { loading.value = false }
}

function selectSeat(seat) {
  if (seat.occupied && !seat.isMe) showToast(`${seat.nickname} 已入座`)
  else if (!seat.occupied) showToast(`选择座位 ${seat.id}`)
}

async function leaveMySeat() {
  showLoading()
  try { await leaveSeat({}); showToast('已取消入座'); loadData() } finally { hideLoading() }
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.page { min-height: 100vh; background: #F8F9FB; padding-bottom: 240rpx; }
.navbar {
  display: flex; align-items: center; height: 88rpx; padding: 0 32rpx;
  .nav-title { flex: 1; text-align: center; font-size: 36rpx; font-weight: 700; color: #7B61FF; }
  .nav-left, .nav-right { width: 60rpx; }
}
.content { padding: 24rpx 32rpx; height: calc(100vh - 300rpx); }
.table-header { text-align: center; margin-bottom: 24rpx; }
.table-name { font-size: 48rpx; font-weight: 700; color: #6B46D1; display: block; }
.status-dot { display: flex; align-items: center; justify-content: center; gap: 8rpx; margin: 12rpx 0; font-size: 26rpx; color: #00C08B;
  .dot { width: 12rpx; height: 12rpx; border-radius: 50%; background: #00C08B; }
}
.update-time { font-size: 24rpx; color: #999; }
.stats-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 24rpx; margin-bottom: 32rpx; }
.stat-card {
  background: #F1EFFF; border-radius: 32rpx; padding: 28rpx; text-align: center;
  .stat-label { font-size: 26rpx; color: #666; display: block; }
  .stat-value { font-size: 36rpx; font-weight: 700; color: #7B61FF; margin-top: 8rpx; display: block; }
}
.interaction-area { position: relative; height: 600rpx; }
.game-title {
  position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%);
  font-size: 32rpx; font-weight: 700; color: #7B61FF; z-index: 1;
}
.seats-ring { position: relative; width: 100%; height: 100%; }
.seat-card {
  position: absolute; width: 120rpx; height: 120rpx; border-radius: 24rpx; background: #FFF;
  display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 4rpx;
  box-shadow: var(--shadow-card); font-size: 22rpx; color: #999;
  &.occupied { background: #7B61FF; color: #FFF; }
  &.is-me { border: 4rpx solid #FFD700; }
  .seat-avatar { width: 48rpx; height: 48rpx; border-radius: 50%; }
  .seat-name { font-size: 20rpx; max-width: 100rpx; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
}
.user-status-card {
  position: fixed; left: 32rpx; right: 32rpx; bottom: 140rpx; z-index: 100;
  display: flex; align-items: center; background: #FFF; border-radius: 48rpx;
  padding: 24rpx 32rpx; box-shadow: var(--shadow-float-bar); gap: 16rpx;
  .my-avatar { width: 64rpx; height: 64rpx; border-radius: 50%; }
  .status-text { flex: 1; font-size: 28rpx; color: #333; }
  .cancel-btn { border: 2rpx solid #7B61FF; color: #7B61FF; padding: 10rpx 24rpx; border-radius: 200rpx; font-size: 26rpx; }
}
</style>

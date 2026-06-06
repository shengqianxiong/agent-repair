<template>
  <view class="page">
    <u-navbar title="我的" :autoBack="false" bgColor="#1a1a2e" leftIcon="" />

    <view class="user-header" @click="goMember">
      <u-avatar :src="userInfo.avatar" size="64" />
      <view class="user-info">
        <text class="nickname">{{ userInfo.nickname || '点击登录' }}</text>
        <u-tag :text="userInfo.memberLevelName || '普通会员'" size="mini" type="warning" />
      </view>
      <u-icon name="arrow-right" color="#6b6b80" />
    </view>

    <view class="stats-row">
      <view class="stat-item" @click="goPage('/pages/points/index')">
        <text class="stat-num">{{ userInfo.points || 0 }}</text>
        <text class="stat-label">积分</text>
      </view>
      <view class="stat-item" @click="goPage('/pages/order/list')">
        <text class="stat-num">{{ orderCount }}</text>
        <text class="stat-label">订单</text>
      </view>
      <view class="stat-item" @click="goBooking">
        <text class="stat-num">{{ bookingCount }}</text>
        <text class="stat-label">预约</text>
      </view>
    </view>

    <u-cell-group>
      <u-cell title="我的订单" icon="order" isLink @click="goPage('/pages/order/list')" />
      <u-cell title="我的预约" icon="calendar" isLink @click="goBooking" />
      <u-cell title="会员中心" icon="level" isLink @click="goMember" />
      <u-cell title="我的积分" icon="integral" isLink @click="goPage('/pages/points/index')" />
      <u-cell title="团购核销" icon="coupon" isLink @click="goPage('/pages/coupon/verify')" />
      <u-cell title="消息通知" icon="bell" isLink @click="goPage('/pages/notification/list')" />
    </u-cell-group>

    <AppTabbar active="profile" />
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow, onPullDownRefresh } from '@dcloudio/uni-app'
import { getUserInfo } from '@/api/user'
import { getOrderList } from '@/api/order'
import { getBookingList } from '@/api/booking'
import { goPage } from '@/utils/navigate'

const userInfo = ref({})
const orderCount = ref(0)
const bookingCount = ref(0)

onShow(() => loadData())
onPullDownRefresh(async () => {
  await loadData()
  uni.stopPullDownRefresh()
})

async function loadData() {
  await Promise.allSettled([loadUser(), loadCounts()])
}

async function loadUser() {
  try {
    userInfo.value = await getUserInfo() || {}
  } catch {
    userInfo.value = { nickname: '云享会员', memberLevelName: '普通会员', points: 0 }
  }
}

async function loadCounts() {
  try {
    const orders = await getOrderList({ page: 1, pageSize: 1 })
    orderCount.value = orders?.total || 0
  } catch { orderCount.value = 0 }
  try {
    const bookings = await getBookingList({ page: 1, pageSize: 1 })
    bookingCount.value = bookings?.total || 0
  } catch { bookingCount.value = 0 }
}

function goMember() {
  uni.navigateTo({ url: '/pages/member/index' })
}

function goBooking() {
  uni.navigateTo({ url: '/pages/booking/index' })
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #0f0f1a;
  padding-bottom: 120rpx;
}
.user-header {
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 40rpx 32rpx;
  background: linear-gradient(135deg, #2d1b69, #1a1a2e);
}
.user-info {
  flex: 1;
}
.nickname {
  font-size: 36rpx;
  font-weight: 600;
  color: #fff;
  display: block;
  margin-bottom: 8rpx;
}
.stats-row {
  display: flex;
  background: #1a1a2e;
  margin: 16rpx;
  border-radius: 12rpx;
  padding: 24rpx 0;
}
.stat-item {
  flex: 1;
  text-align: center;
}
.stat-num {
  display: block;
  font-size: 36rpx;
  font-weight: 600;
  color: #7c3aed;
}
.stat-label {
  font-size: 24rpx;
  color: #6b6b80;
  margin-top: 4rpx;
}
</style>

<template>
  <view class="page">
    <u-navbar title="我的" :autoBack="false" bgColor="#1a1a2e" titleStyle="color:#fff"></u-navbar>

    <scroll-view scroll-y class="page-body">
    <view class="user-header" @click="goMember">
      <u-avatar :src="userInfo.avatar || ''" size="64"></u-avatar>
      <view class="user-info">
        <text class="nickname">{{ userInfo.nickname || '点击登录' }}</text>
        <u-tag :text="userInfo.memberLevelName || '普通会员'" type="warning" size="mini" plain></u-tag>
      </view>
      <u-icon name="arrow-right" color="#6b6b80"></u-icon>
    </view>

    <view class="stats-row">
      <view class="stat-item" @click="goPage('/pages/points/index')">
        <text class="stat-value">{{ userInfo.points || 0 }}</text>
        <text class="stat-label">积分</text>
      </view>
      <view class="stat-item" @click="goPage('/pages/order/list')">
        <text class="stat-value">{{ orderCount }}</text>
        <text class="stat-label">订单</text>
      </view>
      <view class="stat-item" @click="goPage('/pages/booking/detail')">
        <text class="stat-value">{{ bookingCount }}</text>
        <text class="stat-label">预约</text>
      </view>
    </view>

    <u-cell-group title="常用功能">
      <u-cell title="我的订单" icon="order" isLink @click="goPage('/pages/order/list')"></u-cell>
      <u-cell title="我的预约" icon="calendar" isLink @click="goBookingList"></u-cell>
      <u-cell title="会员中心" icon="star" isLink @click="goPage('/pages/member/index')"></u-cell>
      <u-cell title="我的积分" icon="integral" isLink @click="goPage('/pages/points/index')"></u-cell>
      <u-cell title="团购核销" icon="scan" isLink @click="goPage('/pages/coupon/verify')"></u-cell>
      <u-cell title="消息通知" icon="bell" isLink @click="goPage('/pages/notification/list')"></u-cell>
    </u-cell-group>
    </scroll-view>

    <app-tabbar current="profile"></app-tabbar>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onPullDownRefresh, onShow } from '@dcloudio/uni-app'
import { getUserInfo, getOrderList, getBookingList } from '@/api/index.js'

const userInfo = ref({})
const orderCount = ref(0)
const bookingCount = ref(0)

async function loadData() {
  try {
    const [user, orders, bookings] = await Promise.allSettled([
      getUserInfo(),
      getOrderList({ page: 1, pageSize: 1 }),
      getBookingList({ page: 1, pageSize: 1 })
    ])
    if (user.status === 'fulfilled') userInfo.value = user.value || {}
    if (orders.status === 'fulfilled') orderCount.value = orders.value?.total ?? 0
    if (bookings.status === 'fulfilled') bookingCount.value = bookings.value?.total ?? 0
  } catch (e) {}
}

function goPage(url) {
  uni.navigateTo({ url })
}

function goMember() {
  uni.navigateTo({ url: '/pages/member/index' })
}

function goBookingList() {
  uni.navigateTo({ url: '/pages/booking/index' })
}

onMounted(loadData)
onShow(loadData)
onPullDownRefresh(async () => {
  await loadData()
  uni.stopPullDownRefresh()
})
</script>

<style lang="scss" scoped>
.page {
  background: #0f0f1a;
}
.user-header {
  display: flex;
  align-items: center;
  gap: 24rpx;
  padding: 48rpx 32rpx;
  background: linear-gradient(135deg, #2d1b69, #1a1a2e);
}
.user-info { flex: 1; }
.nickname { font-size: 36rpx; font-weight: 600; color: #fff; display: block; margin-bottom: 8rpx; }
.stats-row {
  display: flex;
  margin: -32rpx 24rpx 24rpx;
  background: #1a1a2e;
  border-radius: 16rpx;
  padding: 32rpx 0;
}
.stat-item {
  flex: 1;
  text-align: center;
}
.stat-value { font-size: 36rpx; font-weight: 700; color: #7c3aed; display: block; }
.stat-label { font-size: 24rpx; color: #6b6b80; margin-top: 8rpx; display: block; }
</style>

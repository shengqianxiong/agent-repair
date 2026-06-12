<template>
  <view class="dp-page center-page dp-safe-bottom">
    <u-navbar title="我的" :autoBack="false" bgColor="#f6f8fc" titleStyle="font-weight:700;color:#20263a"></u-navbar>

    <scroll-view scroll-y class="content" refresher-enabled :refresher-triggered="refreshing" @refresherrefresh="refreshData">
      <view class="profile-card">
        <view class="profile-top">
          <image class="avatar" :src="summary.avatar" mode="aspectFill"></image>
          <view class="profile-info">
            <text class="nickname">{{ summary.nickname }}</text>
            <text class="user-id">ID：{{ summary.userIdentifier }}</text>
          </view>
          <u-tag text="普通用户" type="primary" size="mini" plain></u-tag>
        </view>
        <view class="balance-panel">
          <view>
            <text class="balance-label">累计返利（元）</text>
            <text class="balance-value">{{ summary.totalRebate }}</text>
          </view>
          <u-button text="提现" size="small" type="primary" shape="circle" :customStyle="withdrawStyle" @click="showComing"></u-button>
        </view>
      </view>

      <view class="stat-grid">
        <view class="stat-card">
          <text class="stat-value">{{ summary.pendingAudit }}</text>
          <text class="stat-label">待审核</text>
        </view>
        <view class="stat-card">
          <text class="stat-value">{{ summary.paidAmount }}</text>
          <text class="stat-label">已到账</text>
        </view>
        <view class="stat-card">
          <text class="stat-value">{{ summary.taskCount }}</text>
          <text class="stat-label">评价任务</text>
        </view>
      </view>

      <view class="menu-card">
        <view class="menu-item" @click="goRecords">
          <view class="menu-icon blue">
            <u-icon name="order" color="#1463ff" size="22"></u-icon>
          </view>
          <view class="menu-copy">
            <text class="menu-title">评价记录</text>
            <text class="menu-desc">查看扫码任务、审核状态和拒绝原因</text>
          </view>
          <u-icon name="arrow-right" color="#98a2b3" size="16"></u-icon>
        </view>
        <view class="menu-item" @click="goLatestRebate">
          <view class="menu-icon orange">
            <u-icon name="red-packet" color="#ff9f18" size="22"></u-icon>
          </view>
          <view class="menu-copy">
            <text class="menu-title">返利明细</text>
            <text class="menu-desc">追踪最近一笔返利审核与到账进度</text>
          </view>
          <u-icon name="arrow-right" color="#98a2b3" size="16"></u-icon>
        </view>
        <view class="menu-item" @click="goTask">
          <view class="menu-icon green">
            <u-icon name="scan" color="#25c06d" size="22"></u-icon>
          </view>
          <view class="menu-copy">
            <text class="menu-title">继续做任务</text>
            <text class="menu-desc">返回扫码任务页复制文案并提交截图</text>
          </view>
          <u-icon name="arrow-right" color="#98a2b3" size="16"></u-icon>
        </view>
      </view>

      <view class="notice-card">
        <u-icon name="info-circle-fill" color="#1463ff" size="18"></u-icon>
        <text>返利以商家审核结果为准，若被拒绝可按原因重新提交清晰截图。</text>
      </view>
    </scroll-view>

    <app-tabbar current="center"></app-tabbar>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { demoTasks, getUserSummary } from '@/api/task.js'
import { ensureVisitorId } from '@/utils/auth.js'

const refreshing = ref(false)
const summary = ref({
  avatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=240&q=80',
  nickname: '用户昵称',
  userIdentifier: ensureVisitorId(),
  totalRebate: '28.50',
  pendingAudit: 1,
  paidAmount: '26.50',
  taskCount: 3,
  latestRebateId: demoTasks[0].rebateId
})

const withdrawStyle = {
  width: '126rpx',
  height: '58rpx',
  fontWeight: '700',
  background: '#1463ff',
  border: '0'
}

async function loadSummary() {
  try {
    const data = await getUserSummary()
    summary.value = {
      ...summary.value,
      ...data,
      totalRebate: data.totalRebate ?? data.totalAmount ?? summary.value.totalRebate,
      pendingAudit: data.pendingAudit ?? data.auditCount ?? summary.value.pendingAudit,
      paidAmount: data.paidAmount ?? data.paidRebate ?? summary.value.paidAmount
    }
  } catch (error) {}
}

async function refreshData() {
  refreshing.value = true
  await loadSummary()
  refreshing.value = false
}

function goRecords() {
  uni.switchTab({ url: '/pages/user/records' })
}

function goLatestRebate() {
  uni.navigateTo({ url: `/pages/rebate/detail?id=${summary.value.latestRebateId}` })
}

function goTask() {
  uni.switchTab({ url: '/pages/task/index' })
}

function showComing() {
  uni.showToast({ title: '返利到账由平台统一处理', icon: 'none' })
}

onShow(loadSummary)
</script>

<style lang="scss" scoped>
.content {
  height: calc(100vh - 96rpx);
  padding: 20rpx 28rpx 40rpx;
  box-sizing: border-box;
}

.profile-card {
  padding: 30rpx;
  border-radius: 32rpx;
  background: linear-gradient(150deg, #1463ff, #2e88ff);
  color: #fff;
  box-shadow: 0 18rpx 46rpx rgba(20, 99, 255, 0.26);
}

.profile-top {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.avatar {
  width: 92rpx;
  height: 92rpx;
  border-radius: 50%;
  border: 4rpx solid rgba(255, 255, 255, 0.72);
}

.profile-info {
  flex: 1;
}

.nickname {
  display: block;
  font-size: 34rpx;
  font-weight: 800;
}

.user-id {
  display: block;
  margin-top: 8rpx;
  color: rgba(255, 255, 255, 0.72);
  font-size: 23rpx;
}

.balance-panel {
  margin-top: 34rpx;
  padding: 24rpx;
  border-radius: 26rpx;
  background: rgba(255, 255, 255, 0.16);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.balance-label {
  display: block;
  color: rgba(255, 255, 255, 0.76);
  font-size: 24rpx;
}

.balance-value {
  display: block;
  margin-top: 8rpx;
  font-size: 48rpx;
  line-height: 58rpx;
  font-weight: 900;
}

.stat-grid {
  margin: 24rpx 0;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 18rpx;
}

.stat-card,
.menu-card,
.notice-card {
  background: #fff;
  border-radius: 28rpx;
  box-shadow: 0 16rpx 42rpx rgba(21, 48, 105, 0.08);
}

.stat-card {
  padding: 26rpx 12rpx;
  text-align: center;
}

.stat-value {
  display: block;
  color: #20263a;
  font-size: 34rpx;
  font-weight: 900;
}

.stat-label {
  display: block;
  margin-top: 8rpx;
  color: #98a2b3;
  font-size: 23rpx;
}

.menu-card {
  padding: 6rpx 28rpx;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 28rpx 0;
  border-bottom: 1rpx solid #edf0f5;
}

.menu-item:last-child {
  border-bottom: 0;
}

.menu-icon {
  width: 72rpx;
  height: 72rpx;
  border-radius: 22rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.menu-icon.blue {
  background: #edf4ff;
}

.menu-icon.orange {
  background: #fff6e8;
}

.menu-icon.green {
  background: #eafaf2;
}

.menu-copy {
  flex: 1;
}

.menu-title {
  display: block;
  color: #20263a;
  font-size: 29rpx;
  font-weight: 800;
}

.menu-desc {
  display: block;
  margin-top: 8rpx;
  color: #98a2b3;
  font-size: 23rpx;
}

.notice-card {
  margin-top: 24rpx;
  padding: 24rpx;
  display: flex;
  gap: 12rpx;
  color: #626b82;
  font-size: 24rpx;
  line-height: 38rpx;
}
</style>

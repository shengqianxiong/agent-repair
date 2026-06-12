<template>
  <view class="dp-page records-page dp-safe-bottom">
    <u-navbar title="评价记录" :autoBack="false" bgColor="#f6f8fc" titleStyle="font-weight:700;color:#20263a"></u-navbar>

    <view class="filter-bar">
      <view
        v-for="item in statusTabs"
        :key="item.value"
        class="filter-chip"
        :class="{ active: currentStatus === item.value }"
        @click="changeStatus(item.value)"
      >
        {{ item.label }}
      </view>
    </view>

    <scroll-view scroll-y class="content" refresher-enabled :refresher-triggered="refreshing" @refresherrefresh="refreshData">
      <view v-if="filteredTasks.length" class="record-list">
        <view v-for="task in filteredTasks" :key="task.taskId" class="record-card" @click="goRebate(task)">
          <image class="record-image" :src="task.productImage" mode="aspectFill"></image>
          <view class="record-main">
            <view class="record-head">
              <text class="record-title">{{ task.productName }}</text>
              <text class="record-status" :class="task.status">{{ task.statusText }}</text>
            </view>
            <text class="record-desc">{{ task.activityTitle }}</text>
            <view class="record-meta">
              <text>提交：{{ task.submitTime }}</text>
              <text class="amount">¥{{ task.rebateAmount }}</text>
            </view>
            <view v-if="task.rejectReason" class="reject-box">
              <u-icon name="warning-fill" color="#ff5a5f" size="14"></u-icon>
              <text>{{ task.rejectReason }}</text>
            </view>
          </view>
        </view>
      </view>
      <view v-else class="empty-box">
        <u-empty mode="order" text="暂无评价记录"></u-empty>
        <u-button type="primary" text="去做任务" shape="circle" :customStyle="emptyButtonStyle" @click="goTask"></u-button>
      </view>
    </scroll-view>

    <app-tabbar current="records"></app-tabbar>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onPullDownRefresh, onShow } from '@dcloudio/uni-app'
import { demoTasks, getUserTasks } from '@/api/task.js'

const currentStatus = ref('all')
const refreshing = ref(false)
const tasks = ref([...demoTasks])

const statusTabs = [
  { label: '全部', value: 'all' },
  { label: '审核中', value: 'auditing' },
  { label: '已到账', value: 'paid' },
  { label: '已拒绝', value: 'rejected' }
]

const emptyButtonStyle = {
  width: '260rpx',
  height: '82rpx',
  marginTop: '24rpx',
  background: '#1463ff',
  border: '0',
  fontWeight: '700'
}

const filteredTasks = computed(() => {
  if (currentStatus.value === 'all') return tasks.value
  return tasks.value.filter((task) => task.status === currentStatus.value)
})

async function loadTasks() {
  try {
    const data = await getUserTasks({
      status: currentStatus.value === 'all' ? '' : currentStatus.value
    })
    const list = data.records || data.list || data || []
    tasks.value = list.length ? list : demoTasks
  } catch (error) {
    tasks.value = [...demoTasks]
  }
}

async function refreshData() {
  refreshing.value = true
  await loadTasks()
  refreshing.value = false
  uni.stopPullDownRefresh()
}

function changeStatus(status) {
  if (currentStatus.value === status) return
  currentStatus.value = status
  loadTasks()
}

function goRebate(task) {
  uni.navigateTo({ url: `/pages/rebate/detail?id=${task.rebateId || task.taskId}` })
}

function goTask() {
  uni.switchTab({ url: '/pages/task/index' })
}

onShow(loadTasks)
onPullDownRefresh(refreshData)
</script>

<style lang="scss" scoped>
.records-page {
  padding-bottom: 24rpx;
}

.filter-bar {
  padding: 16rpx 28rpx 20rpx;
  display: flex;
  gap: 14rpx;
  background: #f6f8fc;
}

.filter-chip {
  padding: 14rpx 24rpx;
  border-radius: 999rpx;
  background: #fff;
  color: #626b82;
  font-size: 24rpx;
  box-shadow: 0 8rpx 26rpx rgba(21, 48, 105, 0.05);
}

.filter-chip.active {
  background: #1463ff;
  color: #fff;
  font-weight: 800;
}

.content {
  height: calc(100vh - 166rpx);
  padding: 0 28rpx 36rpx;
  box-sizing: border-box;
}

.record-list {
  display: grid;
  gap: 22rpx;
}

.record-card {
  padding: 22rpx;
  border-radius: 28rpx;
  background: #fff;
  box-shadow: 0 16rpx 42rpx rgba(21, 48, 105, 0.08);
  display: flex;
  gap: 20rpx;
}

.record-image {
  width: 132rpx;
  height: 132rpx;
  border-radius: 22rpx;
  flex-shrink: 0;
}

.record-main {
  flex: 1;
  min-width: 0;
}

.record-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12rpx;
}

.record-title {
  color: #20263a;
  font-size: 29rpx;
  font-weight: 800;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.record-status {
  flex-shrink: 0;
  padding: 6rpx 14rpx;
  border-radius: 999rpx;
  font-size: 22rpx;
  font-weight: 700;
}

.record-status.auditing {
  color: #1463ff;
  background: #edf4ff;
}

.record-status.paid {
  color: #25c06d;
  background: #eafaf2;
}

.record-status.rejected {
  color: #ff5a5f;
  background: #fff0f0;
}

.record-desc {
  display: block;
  margin-top: 10rpx;
  color: #98a2b3;
  font-size: 24rpx;
  line-height: 34rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.record-meta {
  margin-top: 14rpx;
  display: flex;
  justify-content: space-between;
  color: #98a2b3;
  font-size: 23rpx;
}

.amount {
  color: #ff8a00;
  font-weight: 900;
}

.reject-box {
  margin-top: 16rpx;
  padding: 12rpx 14rpx;
  border-radius: 16rpx;
  background: #fff6f6;
  color: #ff5a5f;
  display: flex;
  gap: 8rpx;
  font-size: 22rpx;
  line-height: 32rpx;
}

.empty-box {
  padding-top: 180rpx;
  text-align: center;
}
</style>

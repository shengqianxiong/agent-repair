<template>
  <view class="page">
    <view class="balance-card">
      <text class="label">积分余额</text>
      <text class="balance">{{ balance }}</text>
      <u-button size="small" type="primary" text="去兑换" @click="goRedeem" />
    </view>

    <view class="section-title">积分流水</view>
    <u-list v-if="records.length">
      <u-list-item v-for="item in records" :key="item.id">
        <u-cell
          :title="item.source || item.type"
          :label="formatDateTime(item.createTime)"
          :value="`${item.type === '消费' || item.type === '兑换' ? '-' : '+'}${item.points}`"
        />
      </u-list-item>
    </u-list>
    <u-empty v-else mode="list" text="暂无流水" />
    <u-loadmore v-if="records.length" :status="loadStatus" />
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow, onPullDownRefresh } from '@dcloudio/uni-app'
import { getPointsBalance, getPointsRecords } from '@/api/points'
import { formatDateTime } from '@/utils/navigate'

const balance = ref(0)
const records = ref([])
const loadStatus = ref('loadmore')

onShow(() => loadData())
onPullDownRefresh(async () => {
  await loadData()
  uni.stopPullDownRefresh()
})

async function loadData() {
  await Promise.allSettled([loadBalance(), loadRecords()])
}

async function loadBalance() {
  try {
    const data = await getPointsBalance()
    balance.value = data?.balance ?? data ?? 0
  } catch { balance.value = 0 }
}

async function loadRecords() {
  loadStatus.value = 'loading'
  try {
    const data = await getPointsRecords({ page: 1, pageSize: 50 })
    records.value = data?.list || []
    loadStatus.value = 'nomore'
  } catch {
    records.value = []
    loadStatus.value = 'nomore'
  }
}

function goRedeem() {
  uni.navigateTo({ url: '/pages/points/redeem' })
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #0f0f1a;
  padding: 24rpx;
}
.balance-card {
  background: linear-gradient(135deg, #2d1b69, #1a1a2e);
  border-radius: 16rpx;
  padding: 40rpx;
  text-align: center;
}
.label {
  font-size: 28rpx;
  color: #a0a0b8;
  display: block;
}
.balance {
  font-size: 72rpx;
  font-weight: 700;
  color: #f59e0b;
  display: block;
  margin: 16rpx 0;
}
.section-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #fff;
  margin: 32rpx 0 16rpx;
}
</style>

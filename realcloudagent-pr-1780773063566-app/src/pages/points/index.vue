<template>
  <view class="page">
    <view class="balance-card">
      <text class="label">当前积分</text>
      <text class="balance">{{ balance }}</text>
      <u-button type="primary" size="small" text="积分兑换" @click="goRedeem"></u-button>
    </view>

    <view class="section-title">积分流水</view>
    <view v-if="records.length" class="record-list">
      <u-cell
        v-for="item in records"
        :key="item.id"
        :title="item.source || item.type"
        :label="formatTime(item.createTime)"
        :value="formatPoints(item)"
      ></u-cell>
      <u-loadmore :status="loadStatus"></u-loadmore>
    </view>
    <u-empty v-else mode="list" text="暂无流水"></u-empty>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onPullDownRefresh, onReachBottom } from '@dcloudio/uni-app'
import { getPointsBalance, getPointsRecords } from '@/api/index.js'

const balance = ref(0)
const records = ref([])
const page = ref(1)
const loadStatus = ref('loadmore')

function formatTime(t) {
  if (!t) return ''
  return String(t).slice(0, 16).replace('T', ' ')
}

function formatPoints(item) {
  const p = item.points || 0
  const prefix = item.type === '消费' || p < 0 ? '' : '+'
  return `${prefix}${p}`
}

async function loadBalance() {
  try {
    const data = await getPointsBalance()
    balance.value = data?.balance ?? data ?? 0
  } catch (e) {}
}

async function loadRecords(reset = false) {
  if (reset) {
    page.value = 1
    records.value = []
  }
  loadStatus.value = 'loading'
  try {
    const data = await getPointsRecords({ page: page.value, pageSize: 20 })
    const items = data?.list || data || []
    records.value = reset ? items : [...records.value, ...items]
    loadStatus.value = items.length < 20 ? 'nomore' : 'loadmore'
  } catch (e) {
    loadStatus.value = 'loadmore'
  }
}

function goRedeem() {
  uni.navigateTo({ url: '/pages/points/redeem' })
}

onMounted(async () => {
  await loadBalance()
  await loadRecords(true)
})
onPullDownRefresh(async () => {
  await loadBalance()
  await loadRecords(true)
  uni.stopPullDownRefresh()
})
onReachBottom(() => {
  if (loadStatus.value === 'loadmore') {
    page.value++
    loadRecords()
  }
})
</script>

<style lang="scss" scoped>
.page { min-height: 100vh; background: #0f0f1a; }
.balance-card {
  margin: 32rpx;
  padding: 48rpx;
  background: linear-gradient(135deg, #2d1b69, #1a1a2e);
  border-radius: 20rpx;
  text-align: center;
}
.label { color: #a0a0b8; display: block; }
.balance { font-size: 72rpx; font-weight: 700; color: #f59e0b; display: block; margin: 16rpx 0 24rpx; }
.section-title { padding: 16rpx 32rpx; color: #a0a0b8; }
</style>

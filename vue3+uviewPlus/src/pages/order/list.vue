<template>
  <view class="page">
    <u-tabs :list="tabList" :current="currentTab" @change="onTabChange" />

    <view v-if="list.length" class="order-list">
      <view v-for="item in list" :key="item.id" class="order-card" @click="goDetail(item.id)">
        <view class="card-header">
          <text class="order-no">{{ item.orderNo }}</text>
          <u-tag :text="item.status" size="mini" :type="getStatusType(item.status)" />
        </view>
        <view class="card-body">
          <text v-for="(oi, idx) in (item.items || []).slice(0, 2)" :key="idx" class="item-name">
            {{ oi.productName }} x{{ oi.quantity }}
          </text>
        </view>
        <view class="card-footer">
          <text class="time">{{ formatDateTime(item.createTime) }}</text>
          <text class="amount">¥{{ formatPrice(item.payAmount || item.totalAmount) }}</text>
        </view>
      </view>
      <u-loadmore :status="loadStatus" />
    </view>
    <u-empty v-else mode="order" text="暂无订单" />
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow, onPullDownRefresh } from '@dcloudio/uni-app'
import { getOrderList } from '@/api/order'
import { formatPrice, formatDateTime } from '@/utils/navigate'

const tabList = [
  { name: '全部' },
  { name: '待支付' },
  { name: '制作中' },
  { name: '已完成' }
]
const statusFilter = ['', '待支付', '制作中', '已完成']
const currentTab = ref(0)
const list = ref([])
const page = ref(1)
const loadStatus = ref('loadmore')
const finished = ref(false)

onShow(() => refresh())
onPullDownRefresh(async () => {
  await refresh()
  uni.stopPullDownRefresh()
})

function onTabChange(index) {
  currentTab.value = typeof index === 'object' ? index.index : index
  refresh()
}

async function refresh() {
  page.value = 1
  finished.value = false
  list.value = []
  await fetchList()
}

async function fetchList() {
  loadStatus.value = 'loading'
  try {
    const params = { page: page.value, pageSize: 20 }
    const status = statusFilter[currentTab.value]
    if (status) params.status = status
    const data = await getOrderList(params)
    const items = data?.list || []
    list.value = page.value === 1 ? items : [...list.value, ...items]
    finished.value = items.length < 20
    loadStatus.value = finished.value ? 'nomore' : 'loadmore'
  } catch {
    list.value = []
    loadStatus.value = 'nomore'
  }
}

function getStatusType(status) {
  if (status === '已完成') return 'success'
  if (status === '已取消') return 'error'
  if (status === '待支付') return 'warning'
  return 'primary'
}

function goDetail(id) {
  uni.navigateTo({ url: `/pages/order/detail?id=${id}` })
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #0f0f1a;
}
.order-list {
  padding: 16rpx;
}
.order-card {
  background: #1a1a2e;
  border-radius: 12rpx;
  padding: 20rpx;
  margin-bottom: 16rpx;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.order-no {
  font-size: 26rpx;
  color: #a0a0b8;
}
.card-body {
  margin: 16rpx 0;
}
.item-name {
  display: block;
  font-size: 28rpx;
  color: #fff;
  margin-bottom: 4rpx;
}
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.time {
  font-size: 24rpx;
  color: #6b6b80;
}
.amount {
  font-size: 32rpx;
  color: #f59e0b;
  font-weight: 600;
}
</style>

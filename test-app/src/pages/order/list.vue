<template>
  <view class="page page-fixed">
    <u-tabs :list="tabList" :current="currentTab" @change="onTabChange" lineColor="#7c3aed"></u-tabs>

    <scroll-view scroll-y class="page-body" :show-scrollbar="false" @scrolltolower="loadMore">
    <view v-if="orders.length" class="order-list">
      <view v-for="order in orders" :key="order.id" class="order-card" @click="goDetail(order.id)">
        <view class="card-header">
          <text class="order-no">{{ order.orderNo }}</text>
          <u-tag :text="getStatus(order.status).text" :type="getStatus(order.status).type" size="mini"></u-tag>
        </view>
        <view v-for="item in (order.items || []).slice(0, 2)" :key="item.id" class="card-item">
          <text>{{ item.productName }} x{{ item.quantity }}</text>
        </view>
        <view class="card-footer">
          <text class="time">{{ formatTime(order.createTime) }}</text>
          <text class="amount">¥{{ formatPrice(order.payAmount || order.totalAmount) }}</text>
        </view>
      </view>
      <u-loadmore :status="loadStatus"></u-loadmore>
    </view>
    <u-empty v-else mode="order" text="暂无订单"></u-empty>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getOrderList } from '@/api/index.js'
import { formatPrice, ORDER_STATUS_MAP } from '@/utils/common.js'

const tabList = [
  { name: '全部' },
  { name: '待支付' },
  { name: '制作中' },
  { name: '已完成' }
]
const currentTab = ref(0)
const orders = ref([])
const page = ref(1)
const loadStatus = ref('loadmore')

function getStatus(status) {
  return ORDER_STATUS_MAP[status] || { text: status, type: 'info' }
}

function formatTime(t) {
  if (!t) return ''
  return String(t).slice(0, 16).replace('T', ' ')
}

function getStatusFilter() {
  const map = ['', '待支付', '制作中', '已完成']
  return map[currentTab.value] || ''
}

async function loadData(reset = false) {
  if (reset) {
    page.value = 1
    orders.value = []
  }
  loadStatus.value = 'loading'
  try {
    const data = await getOrderList({
      page: page.value,
      pageSize: 10,
      status: getStatusFilter()
    })
    const items = data?.list || []
    orders.value = reset ? items : [...orders.value, ...items]
    loadStatus.value = items.length < 10 ? 'nomore' : 'loadmore'
  } catch (e) {
    loadStatus.value = 'loadmore'
  }
}

function onTabChange(e) {
  currentTab.value = e.index
  loadData(true)
}

function goDetail(id) {
  uni.navigateTo({ url: `/pages/order/detail?id=${id}` })
}

function loadMore() {
  if (loadStatus.value === 'loadmore') {
    page.value++
    loadData()
  }
}

onMounted(() => loadData(true))
</script>

<style lang="scss" scoped>
.page { background: #0f0f1a; }
.order-list { padding: 16rpx 24rpx; }
.order-card {
  background: #1a1a2e;
  border-radius: 12rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}
.order-no { font-size: 26rpx; color: #a0a0b8; }
.card-item { color: #e8e8f0; font-size: 26rpx; padding: 4rpx 0; }
.card-footer {
  display: flex;
  justify-content: space-between;
  margin-top: 16rpx;
  padding-top: 16rpx;
  border-top: 1rpx solid #2a2a40;
}
.time { color: #6b6b80; font-size: 24rpx; }
.amount { color: #f59e0b; font-weight: 600; }
</style>

<template>
  <view class="bar-page">
    <u-tabs :list="tabs" :current="currentTab" @change="onTabChange" lineColor="#6c5ce7" />

    <view v-if="!orders.length && !loading" class="empty-wrap">
      <u-empty mode="order" text="暂无订单" />
    </view>

    <view v-for="order in orders" :key="order.id" class="bar-card order-card" @click="goDetail(order.id)">
      <view class="order-header">
        <text class="order-no">{{ order.orderNo }}</text>
        <u-tag :text="order.status" size="mini" :type="statusType(order.status)" />
      </view>
      <view v-for="item in (order.items || []).slice(0, 3)" :key="item.id" class="order-item">
        <text>{{ item.productName }} x{{ item.quantity }}</text>
      </view>
      <view class="order-footer">
        <text class="time">{{ formatDateTime(order.createTime) }}</text>
        <text class="price-text">¥{{ formatPrice(order.payAmount || order.totalAmount) }}</text>
      </view>
    </view>

    <u-loadmore v-if="orders.length" :status="loadStatus" />
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow, onPullDownRefresh, onReachBottom } from '@dcloudio/uni-app'
import { getOrderList } from '@/api/order'
import { formatPrice, formatDateTime } from '@/utils/request'

const tabs = [
  { name: '全部' },
  { name: '待支付' },
  { name: '制作中' },
  { name: '已完成' }
]
const statusMap = ['', '待支付', '制作中', '已完成']
const currentTab = ref(0)
const orders = ref([])
const loading = ref(false)
const loadStatus = ref('loadmore')
const page = ref(1)
const total = ref(0)

function statusType(status) {
  const map = { '待支付': 'warning', '制作中': 'primary', '已完成': 'success', '已取消': 'info' }
  return map[status] || 'info'
}

async function loadOrders(reset = true) {
  if (reset) {
    page.value = 1
    orders.value = []
  }
  loading.value = true
  loadStatus.value = 'loading'
  try {
    const status = statusMap[currentTab.value]
    const res = await getOrderList({ page: page.value, pageSize: 10, status: status || undefined })
    const list = res?.list || []
    total.value = res?.total || 0
    orders.value = reset ? list : [...orders.value, ...list]
    loadStatus.value = orders.value.length >= total.value ? 'nomore' : 'loadmore'
  } finally {
    loading.value = false
  }
}

function onTabChange(e) {
  currentTab.value = e.index
  loadOrders(true)
}

function goDetail(id) {
  uni.navigateTo({ url: `/pages/order/detail?id=${id}` })
}

onShow(() => loadOrders(true))
onPullDownRefresh(async () => { await loadOrders(true); uni.stopPullDownRefresh() })
onReachBottom(() => {
  if (loadStatus.value === 'loadmore') {
    page.value++
    loadOrders(false)
  }
})
</script>

<style lang="scss" scoped>
.order-card {
  margin-top: 0;
}

.order-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16rpx;
}

.order-no {
  font-size: 26rpx;
  color: #666;
}

.order-item {
  font-size: 26rpx;
  color: #333;
  padding: 8rpx 0;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  margin-top: 16rpx;
  padding-top: 16rpx;
  border-top: 1rpx solid #f0f0f0;

  .time {
    font-size: 22rpx;
    color: #999;
  }
}

.empty-wrap {
  padding-top: 120rpx;
}
</style>

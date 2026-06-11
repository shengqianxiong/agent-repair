<template>
  <view class="order-page page-container">
    <view class="status-tabs card-box">
      <view
        v-for="tab in tabs"
        :key="tab.value"
        class="tab-item"
        :class="{ active: activeTab === tab.value }"
        @click="activeTab = tab.value"
      >
        {{ tab.label }}
      </view>
    </view>

    <scroll-view scroll-y class="order-scroll">
      <view v-for="item in filteredOrders" :key="item.orderNo" class="order-card card-box">
        <view class="order-top">
          <view class="order-no">订单号：{{ item.orderNo }}</view>
          <view class="order-status" :class="item.statusClass">{{ item.statusText }}</view>
        </view>

        <view class="order-meta">🗓 {{ item.reserveDate }} {{ item.reserveSlot }}</view>
        <view class="order-meta">📍 {{ item.address }}</view>
        <view class="order-meta">回收类型：{{ item.clothTypes.join('、') }}</view>

        <view class="order-footer">
          <view>
            <view class="weight-text">环保金：<text class="reward">¥{{ item.rewardAmount }}</text></view>
            <view class="weight-text">实际重量：<text class="weight">{{ item.realWeight }}kg</text></view>
          </view>
          <view class="actions">
            <u-button
              v-if="item.status === 'PENDING'"
              size="mini"
              plain
              type="primary"
              text="取消订单"
              @click="handleCancel(item)"
            />
            <u-button size="mini" type="primary" text="查看详情" @click="showDetail(item)" />
          </view>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { onPullDownRefresh } from '@dcloudio/uni-app'
import { computed, onMounted, ref, watch } from 'vue'
import { cancelOrder, fetchOrderList } from '@/api'

const tabs = [
  { label: '全部', value: 'ALL' },
  { label: '待上门', value: 'PENDING' },
  { label: '已取件', value: 'PICKED' },
  { label: '已完成', value: 'COMPLETED' },
  { label: '已取消', value: 'CANCELLED' }
]

const activeTab = ref('ALL')
const orderList = ref([
  {
    orderNo: '202405230001',
    status: 'PENDING',
    statusText: '待上门',
    statusClass: 'pending',
    reserveDate: '2024-05-26（周日）',
    reserveSlot: '14:00-16:00',
    address: '北京市朝阳区建国路SOHO现代城18层1501室',
    clothTypes: ['成人衣', '儿童衣', '鞋子'],
    rewardAmount: '6.30',
    realWeight: '12.6'
  },
  {
    orderNo: '202405200089',
    status: 'PICKED',
    statusText: '已取件',
    statusClass: 'picked',
    reserveDate: '2024-05-20（周一）',
    reserveSlot: '09:00-11:00',
    address: '北京市朝阳区建国路SOHO现代城18层1501室',
    clothTypes: ['家纺'],
    rewardAmount: '6.30',
    realWeight: '12.6'
  },
  {
    orderNo: '202405180056',
    status: 'COMPLETED',
    statusText: '已完成',
    statusClass: 'done',
    reserveDate: '2024-05-18（周六）',
    reserveSlot: '14:00-16:00',
    address: '北京市朝阳区建国路SOHO现代城18层1501室',
    clothTypes: ['鞋包'],
    rewardAmount: '4.20',
    realWeight: '8.4'
  }
])

const filteredOrders = computed(() => {
  if (activeTab.value === 'ALL') return orderList.value
  return orderList.value.filter((item) => item.status === activeTab.value)
})

async function loadOrders() {
  try {
    const data = await fetchOrderList({ status: activeTab.value })
    if (Array.isArray(data?.records)) {
      orderList.value = data.records.map((item) => ({
        ...item,
        statusClass: mapStatusClass(item.status),
        statusText: mapStatusText(item.status)
      }))
    }
  } catch (error) {
    // mock 数据兜底
  } finally {
    uni.stopPullDownRefresh()
  }
}

function mapStatusText(status) {
  return (
    {
      PENDING: '待上门',
      PICKED: '已取件',
      COMPLETED: '已完成',
      CANCELLED: '已取消'
    }[status] || '处理中'
  )
}

function mapStatusClass(status) {
  return (
    {
      PENDING: 'pending',
      PICKED: 'picked',
      COMPLETED: 'done',
      CANCELLED: 'cancel'
    }[status] || 'pending'
  )
}

async function handleCancel(item) {
  try {
    await cancelOrder({ orderNo: item.orderNo, reason: '时间冲突，重新预约' })
    item.status = 'CANCELLED'
    item.statusClass = 'cancel'
    item.statusText = '已取消'
    uni.showToast({ title: '订单已取消', icon: 'success' })
  } catch (error) {
    uni.showToast({ title: '取消失败，请联系客服', icon: 'none' })
  }
}

function showDetail(item) {
  uni.showModal({
    title: '订单详情',
    content: `订单号：${item.orderNo}\n状态：${item.statusText}\n地址：${item.address}`,
    showCancel: false
  })
}

watch(activeTab, () => {
  loadOrders()
})

onPullDownRefresh(() => {
  loadOrders()
})

onMounted(() => {
  loadOrders()
})
</script>

<style lang="scss" scoped>
.order-page {
  padding-top: 20rpx;
}

.status-tabs {
  padding: 10rpx;
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 8rpx;
  margin-bottom: 16rpx;
}

.tab-item {
  border-radius: 999rpx;
  text-align: center;
  font-size: 22rpx;
  color: #707780;
  padding: 14rpx 0;
  background: #f5f7f9;
}

.tab-item.active {
  color: #1f8c48;
  background: #e4f6eb;
  font-weight: 600;
}

.order-scroll {
  height: calc(100vh - 230rpx - env(safe-area-inset-bottom));
  padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
}

.order-card {
  padding: 24rpx;
  margin-bottom: 16rpx;
}

.order-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-no {
  color: #2f343c;
  font-size: 24rpx;
  font-weight: 600;
}

.order-status {
  font-size: 24rpx;
  font-weight: 600;
}

.order-status.pending {
  color: #ff9a2d;
}

.order-status.picked {
  color: #2eae5e;
}

.order-status.done {
  color: #2eae5e;
}

.order-status.cancel {
  color: #a0a5ad;
}

.order-meta {
  margin-top: 10rpx;
  font-size: 22rpx;
  color: #6a707b;
  line-height: 1.4;
}

.order-footer {
  margin-top: 14rpx;
  padding-top: 14rpx;
  border-top: 2rpx solid #edf0f3;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.weight-text {
  font-size: 22rpx;
  color: #616872;
}

.reward {
  color: #ff8a00;
  font-size: 24rpx;
  font-weight: 600;
}

.weight {
  color: #2eae5e;
  font-size: 24rpx;
  font-weight: 600;
}

.actions {
  display: flex;
  gap: 12rpx;
}
</style>

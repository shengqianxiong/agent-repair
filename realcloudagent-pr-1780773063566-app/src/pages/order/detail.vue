<template>
  <view class="page" v-if="order">
    <view class="status-card">
      <u-tag :text="statusInfo.text" :type="statusInfo.type" size="large"></u-tag>
      <text class="order-no">订单号：{{ order.orderNo }}</text>
    </view>

    <u-steps :current="stepCurrent" direction="column">
      <u-steps-item title="待支付"></u-steps-item>
      <u-steps-item title="已支付"></u-steps-item>
      <u-steps-item title="制作中"></u-steps-item>
      <u-steps-item title="已完成"></u-steps-item>
    </u-steps>

    <view class="items">
      <view class="section-title">商品明细</view>
      <view v-for="item in order.items || []" :key="item.id" class="item-row">
        <text>{{ item.productName }} x{{ item.quantity }}</text>
        <text>¥{{ formatPrice(item.subtotal) }}</text>
      </view>
      <view class="total-row">
        <text>实付金额</text>
        <text class="amount">¥{{ formatPrice(order.payAmount || order.totalAmount) }}</text>
      </view>
    </view>

    <view class="actions">
      <u-button v-if="order.status === '待支付'" type="error" text="取消订单" @click="cancel"></u-button>
      <u-button type="primary" text="再来一单" @click="reorder"></u-button>
    </view>
  </view>
  <u-loading-page v-else loading></u-loading-page>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getOrderDetail, cancelOrder } from '@/api/index.js'
import { formatPrice, ORDER_STATUS_MAP } from '@/utils/common.js'

const order = ref(null)

const statusInfo = computed(() => ORDER_STATUS_MAP[order.value?.status] || { text: order.value?.status, type: 'info' })

const stepCurrent = computed(() => {
  const map = { '待支付': 0, '已支付': 1, '制作中': 2, '已完成': 3, '已取消': 0 }
  return map[order.value?.status] ?? 0
})

async function loadDetail(id) {
  try {
    order.value = await getOrderDetail(id)
  } catch (e) {
    uni.navigateBack()
  }
}

async function cancel() {
  uni.showModal({
    title: '提示',
    content: '确定取消订单？',
    success: async (res) => {
      if (res.confirm) {
        try {
          await cancelOrder(order.value.id)
          uni.showToast({ title: '已取消', icon: 'success' })
          loadDetail(order.value.id)
        } catch (e) {}
      }
    }
  })
}

function reorder() {
  uni.switchTab({ url: '/pages/order/index' })
}

onMounted(() => {
  const pages = getCurrentPages()
  const cur = pages[pages.length - 1]
  const id = cur.options?.id || cur.$page?.options?.id
  if (id) loadDetail(id)
})
</script>

<style lang="scss" scoped>
.page { min-height: 100vh; background: #0f0f1a; padding: 32rpx; padding-bottom: 160rpx; }
.status-card { text-align: center; padding: 32rpx; margin-bottom: 32rpx; }
.order-no { display: block; margin-top: 16rpx; color: #6b6b80; font-size: 24rpx; }
.section-title { color: #a0a0b8; margin: 32rpx 0 16rpx; }
.item-row {
  display: flex;
  justify-content: space-between;
  padding: 16rpx 0;
  border-bottom: 1rpx solid #2a2a40;
  color: #e8e8f0;
}
.total-row {
  display: flex;
  justify-content: space-between;
  padding: 24rpx 0;
  font-weight: 600;
}
.amount { color: #f59e0b; font-size: 32rpx; }
.actions {
  position: fixed;
  left: 32rpx;
  right: 32rpx;
  bottom: calc(48rpx + env(safe-area-inset-bottom));
  display: flex;
  gap: 20rpx;
}
</style>

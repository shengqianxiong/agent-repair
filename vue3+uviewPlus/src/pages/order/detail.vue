<template>
  <view class="page" v-if="order">
    <view class="status-card">
      <u-tag :text="order.status" :type="statusType" size="large" />
      <text class="order-no">订单号：{{ order.orderNo }}</text>
    </view>

    <u-steps :current="stepCurrent" direction="row">
      <u-steps-item title="待支付" />
      <u-steps-item title="已支付" />
      <u-steps-item title="制作中" />
      <u-steps-item title="已完成" />
    </u-steps>

    <view class="section">
      <view class="section-title">商品明细</view>
      <view v-for="item in order.items || []" :key="item.id" class="item-row">
        <text>{{ item.productName }} x{{ item.quantity }}</text>
        <text>¥{{ formatPrice(item.subtotal || item.unitPrice * item.quantity) }}</text>
      </view>
    </view>

    <u-cell-group>
      <u-cell title="桌号" :value="order.tableNo || '-'" />
      <u-cell title="备注" :value="order.remark || '无'" />
      <u-cell title="下单时间" :value="formatDateTime(order.createTime)" />
      <u-cell title="实付金额" :value="`¥${formatPrice(order.payAmount || order.totalAmount)}`" />
    </u-cell-group>

    <view class="actions">
      <u-button v-if="order.status === '待支付'" type="error" text="取消订单" @click="doCancel" />
      <u-button type="primary" text="再来一单" @click="reorder" />
    </view>
  </view>
  <u-loading-page :loading="loading" />
</template>

<script setup>
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getOrderDetail, cancelOrder } from '@/api/order'
import { formatPrice, formatDateTime } from '@/utils/navigate'

const order = ref(null)
const loading = ref(true)
let orderId = null

const statusMap = { '待支付': 0, '已支付': 1, '制作中': 2, '已完成': 3, '已取消': -1 }
const stepCurrent = computed(() => statusMap[order.value?.status] ?? 0)
const statusType = computed(() => {
  const s = order.value?.status
  if (s === '已完成') return 'success'
  if (s === '已取消') return 'error'
  if (s === '待支付') return 'warning'
  return 'primary'
})

onLoad((options) => {
  orderId = options.id
  loadDetail()
})

async function loadDetail() {
  loading.value = true
  try {
    order.value = await getOrderDetail(orderId)
  } catch (e) {
    uni.showToast({ title: e.msg || '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

async function doCancel() {
  try {
    await cancelOrder(orderId)
    uni.showToast({ title: '已取消', icon: 'success' })
    loadDetail()
  } catch (e) {
    uni.showToast({ title: e.msg || '取消失败', icon: 'none' })
  }
}

function reorder() {
  uni.switchTab({ url: '/pages/order/index' })
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #0f0f1a;
  padding: 24rpx;
  padding-bottom: 160rpx;
}
.status-card {
  background: #1a1a2e;
  border-radius: 12rpx;
  padding: 32rpx;
  text-align: center;
  margin-bottom: 24rpx;
}
.order-no {
  display: block;
  margin-top: 16rpx;
  font-size: 24rpx;
  color: #6b6b80;
}
.section {
  margin: 24rpx 0;
  background: #1a1a2e;
  border-radius: 12rpx;
  padding: 20rpx;
}
.section-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #fff;
  margin-bottom: 16rpx;
}
.item-row {
  display: flex;
  justify-content: space-between;
  padding: 12rpx 0;
  color: #a0a0b8;
  font-size: 26rpx;
}
.actions {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  gap: 16rpx;
  padding: 20rpx 24rpx;
  background: #1a1a2e;
}
</style>

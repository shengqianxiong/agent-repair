<template>
  <view class="bar-page" v-if="order">
    <view class="bar-card">
      <view class="status-row">
        <u-tag :text="order.status" :type="statusType(order.status)" size="large" />
        <text class="order-no">{{ order.orderNo }}</text>
      </view>
      <u-steps :current="stepCurrent" direction="column">
        <u-steps-item title="待支付" />
        <u-steps-item title="已支付" />
        <u-steps-item title="制作中" />
        <u-steps-item title="已完成" />
      </u-steps>
    </view>

    <view class="bar-card">
      <view class="section-title">商品明细</view>
      <view v-for="item in order.items" :key="item.id" class="detail-item">
        <text>{{ item.productName }} x{{ item.quantity }}</text>
        <text>¥{{ formatPrice(item.subtotal || item.unitPrice * item.quantity) }}</text>
      </view>
      <u-cell title="桌号" :value="order.tableNo || '-'" />
      <u-cell title="备注" :value="order.remark || '无'" />
      <u-cell title="实付金额" :value="'¥' + formatPrice(order.payAmount || order.totalAmount)" />
      <u-cell title="下单时间" :value="formatDateTime(order.createTime)" />
    </view>

    <view class="bottom-bar safe-bottom" v-if="order.status === '待支付' || order.status === '已完成'">
      <u-button v-if="order.status === '待支付'" type="error" text="取消订单" @click="doCancel" />
      <u-button v-if="order.status === '待支付'" type="primary" text="去支付" @click="goPay" />
      <u-button v-if="order.status === '已完成'" type="primary" text="再来一单" @click="reorder" />
    </view>
  </view>
  <u-loading-page v-else loading />
</template>

<script setup>
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getOrderDetail, cancelOrder } from '@/api/order'
import { formatPrice, formatDateTime } from '@/utils/request'

const order = ref(null)
const orderId = ref('')

const stepMap = { '待支付': 0, '已支付': 1, '制作中': 2, '已完成': 3, '已取消': 0 }
const stepCurrent = computed(() => stepMap[order.value?.status] ?? 0)

function statusType(status) {
  const map = { '待支付': 'warning', '制作中': 'primary', '已完成': 'success' }
  return map[status] || 'info'
}

async function loadDetail() {
  order.value = await getOrderDetail(orderId.value)
}

async function doCancel() {
  await cancelOrder(orderId.value)
  uni.showToast({ title: '已取消', icon: 'success' })
  loadDetail()
}

function goPay() {
  uni.navigateTo({
    url: `/pages/payment/index?orderId=${orderId.value}&amount=${order.value.payAmount || order.value.totalAmount}`
  })
}

function reorder() {
  uni.reLaunch({ url: '/pages/order/index' })
}

onLoad((opts) => {
  orderId.value = opts.id
  loadDetail()
})
</script>

<style lang="scss" scoped>
.status-row {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-bottom: 24rpx;
}

.order-no {
  font-size: 24rpx;
  color: #999;
}

.section-title {
  font-weight: bold;
  margin-bottom: 16rpx;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  padding: 12rpx 0;
  font-size: 26rpx;
}

.bottom-bar {
  position: fixed;
  left: 24rpx;
  right: 24rpx;
  bottom: 40rpx;
  display: flex;
  gap: 20rpx;
}
</style>

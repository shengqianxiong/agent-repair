<template>
  <view class="page">
    <LoadingOverlay :show="loading" />
    <AppNavbar title="订单详情" right-icon="share" bg-color="#F8F9FB" />

    <scroll-view scroll-y class="content">
      <view class="status-section">
        <view class="status-badge">{{ order.statusText || '等待出餐中' }}</view>
        <text class="status-time">{{ order.estimatedTime || '预计 5-10 分钟' }}</text>
      </view>

      <view class="pickup-card">
        <text class="pickup-label">取餐号</text>
        <text class="pickup-code">{{ order.pickupCode || 'A086' }}</text>
        <view class="pickup-stats">
          <text>待制作 {{ order.makingCount || 2 }} 杯</text>
          <text class="stat-divider">|</text>
          <text>制作中 {{ order.pendingCount || 5 }} 杯</text>
        </view>
        <view class="show-code-btn" @click="showPickupCode">向店员展示取餐码</view>
      </view>

      <view class="info-card">
        <view class="store-row">
          <u-icon name="map" color="#7B61FF" size="16"></u-icon>
          <text class="store-name">{{ order.store?.name }}</text>
        </view>
        <view v-for="item in order.items" :key="item.name" class="item-row">
          <image class="item-img" :src="item.image" mode="aspectFill" />
          <view class="item-info">
            <text class="item-name">{{ item.name }}</text>
            <text class="item-spec">{{ item.spec }}</text>
          </view>
          <view class="item-price">
            <text>¥{{ item.price }}</text>
            <text class="item-qty">x{{ item.quantity }}</text>
          </view>
        </view>
        <view class="fee-row"><text>商品小计</text><text>¥{{ order.subtotal }}</text></view>
        <view class="fee-row"><text>打包费</text><text>¥{{ order.packingFee }}</text></view>
        <view v-if="order.discount" class="fee-row discount"><text>优惠</text><text>-¥{{ order.discount }}</text></view>
        <view class="total-row"><text>合计</text><text class="total-amount">¥{{ order.totalAmount }}</text></view>
      </view>

      <view class="info-card">
        <view class="kv-row"><text class="kv-label">订单编号</text><text class="kv-value">{{ order.id }}</text></view>
        <view class="kv-row"><text class="kv-label">下单时间</text><text class="kv-value">{{ order.createTime }}</text></view>
        <view class="kv-row">
          <text class="kv-label">支付方式</text>
          <view class="kv-value pay-method">
            <u-icon name="weixin-fill" color="#52C41A" size="16"></u-icon>
            <text>{{ order.payMethod }}</text>
          </view>
        </view>
        <view class="kv-row"><text class="kv-label">备注信息</text><text class="kv-value">{{ order.remark || '无' }}</text></view>
      </view>

      <view class="footer-links">
        <view class="link-item" @click="showToast('联系门店')">
          <u-icon name="phone" color="#7B61FF" size="16"></u-icon><text>联系门店</text>
        </view>
        <view class="link-divider"></view>
        <view class="link-item" @click="showToast('售后帮助')">
          <u-icon name="question-circle" color="#7B61FF" size="16"></u-icon><text>售后帮助</text>
        </view>
      </view>
      <view class="bottom-spacer"></view>
    </scroll-view>

    <view class="bottom-bar">
      <view class="pay-info">
        <text class="pay-label">实付</text>
        <text class="pay-amount">¥{{ order.payAmount }}</text>
      </view>
      <view class="action-btns">
        <view class="btn-outline" @click="reorder">再来一单</view>
        <view class="btn-primary" @click="showPickupCode">订单详情卡</view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getOrderDetail, reorder as reorderApi } from '@/api/index.js'
import { showToast } from '@/utils/common.js'

const loading = ref(false)
const orderId = ref('')
const order = ref({})

async function loadDetail() {
  loading.value = true
  try { order.value = await getOrderDetail(orderId.value) || {} } finally { loading.value = false }
}

function showPickupCode() { showToast(`取餐码: ${order.value.pickupCode}`) }

async function reorder() {
  await reorderApi(orderId.value)
  showToast('已加入购物车')
  uni.switchTab({ url: '/pages/menu/index' })
}

onLoad((query) => { orderId.value = query.id || 'ORD20260606002'; loadDetail() })
</script>

<style lang="scss" scoped>
.page { min-height: 100vh; background: #F8F9FB; }
.content { height: calc(100vh - 200rpx); padding: 0 32rpx; }
.status-section { text-align: center; padding: 32rpx 0; }
.status-badge { display: inline-block; background: #F0E9FF; color: #7B61FF; font-size: 28rpx; font-weight: 600; padding: 12rpx 32rpx; border-radius: 200rpx; }
.status-time { display: block; margin-top: 12rpx; font-size: 26rpx; color: #999; }
.pickup-card {
  background: #FFF; border-radius: 48rpx; padding: 40rpx; text-align: center; margin-bottom: 24rpx; box-shadow: var(--shadow-card-purple);
  .pickup-label { font-size: 26rpx; color: #999; display: block; }
  .pickup-code { font-size: 96rpx; font-weight: 700; color: #7B61FF; display: block; margin: 16rpx 0; }
  .pickup-stats { font-size: 26rpx; color: #666; margin-bottom: 32rpx; }
  .stat-divider { margin: 0 16rpx; color: #EEE; }
  .show-code-btn { background: #7B61FF; color: #FFF; padding: 24rpx; border-radius: 200rpx; font-size: 30rpx; font-weight: 600; box-shadow: var(--shadow-button); }
}
.info-card {
  background: #FFF; border-radius: 32rpx; padding: 32rpx; margin-bottom: 24rpx; box-shadow: var(--shadow-card);
  .store-row { display: flex; align-items: center; gap: 8rpx; margin-bottom: 24rpx; padding-bottom: 24rpx; border-bottom: 1rpx dashed #EEE; }
  .store-name { font-size: 28rpx; font-weight: 600; color: #333; }
  .item-row { display: flex; align-items: center; margin-bottom: 20rpx; }
  .item-img { width: 100rpx; height: 100rpx; border-radius: 16rpx; }
  .item-info { flex: 1; margin-left: 16rpx; }
  .item-name { font-size: 28rpx; color: #333; display: block; }
  .item-spec { font-size: 24rpx; color: #999; }
  .item-price { text-align: right; font-size: 28rpx; color: #333; }
  .item-qty { display: block; font-size: 24rpx; color: #999; }
  .fee-row { display: flex; justify-content: space-between; font-size: 26rpx; color: #666; padding: 8rpx 0;
    &.discount { color: #E64340; }
  }
  .total-row { display: flex; justify-content: space-between; margin-top: 16rpx; padding-top: 16rpx; border-top: 1rpx solid #F0F0F0; font-size: 28rpx; }
  .total-amount { font-size: 40rpx; font-weight: 700; color: #7B61FF; }
  .kv-row { display: flex; justify-content: space-between; padding: 16rpx 0; font-size: 28rpx; }
  .kv-label { color: #999; }
  .kv-value { color: #333; }
  .pay-method { display: flex; align-items: center; gap: 8rpx; }
}
.footer-links {
  display: flex; align-items: center; justify-content: center; padding: 32rpx 0; gap: 32rpx;
  .link-item { display: flex; align-items: center; gap: 8rpx; font-size: 28rpx; color: #7B61FF; }
  .link-divider { width: 1rpx; height: 32rpx; background: #EEE; }
}
.bottom-spacer { height: 160rpx; }
.bottom-bar {
  position: fixed; left: 0; right: 0; bottom: 0; background: #FFF; padding: 20rpx 32rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom)); box-shadow: var(--shadow-bottom-bar);
  display: flex; align-items: center; justify-content: space-between;
  .pay-label { font-size: 24rpx; color: #999; }
  .pay-amount { font-size: 36rpx; font-weight: 700; color: #1A1A1A; display: block; }
  .action-btns { display: flex; gap: 16rpx; }
  .btn-outline { border: 2rpx solid #7B61FF; color: #7B61FF; padding: 16rpx 32rpx; border-radius: 200rpx; font-size: 26rpx; }
  .btn-primary { background: #7B61FF; color: #FFF; padding: 16rpx 32rpx; border-radius: 200rpx; font-size: 26rpx; font-weight: 600; }
}
</style>

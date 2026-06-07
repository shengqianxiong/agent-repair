<template>
  <view class="page">
    <LoadingOverlay :show="loading" />
    <AppNavbar title="确认订单" bg-color="#F8F9FB" />

    <scroll-view scroll-y class="content">
      <view class="card store-card">
        <view class="store-name">{{ store.name }}</view>
        <text class="store-addr">{{ store.address }}</text>
        <view class="dine-tag">{{ store.dineType || '堂食' }}</view>
        <view class="seat-row">
          <text class="seat-label">当前座位</text>
          <text class="seat-no">{{ store.tableNo }}桌</text>
        </view>
      </view>

      <view class="card">
        <text class="card-title">商品清单</text>
        <view v-for="item in cart.items" :key="item.id" class="item-row">
          <image class="item-img" :src="item.image" mode="aspectFill" />
          <view class="item-info">
            <text class="item-name">{{ item.name }}</text>
            <text class="item-spec">{{ item.spec }}</text>
          </view>
          <view class="item-right">
            <text>¥{{ item.price }}</text>
            <text class="qty">x{{ item.quantity }}</text>
          </view>
        </view>
        <view class="fee-row"><text>商品小计</text><text>¥{{ subtotal }}</text></view>
        <view class="fee-row"><text>打包费</text><text>¥2.00</text></view>
        <view class="total-row"><text>合计</text><text class="total">¥{{ total }}</text></view>
      </view>

      <view class="card">
        <text class="card-title">支付方式</text>
        <view class="pay-item" @click="payMethod = 'wechat'">
          <u-icon name="weixin-fill" color="#52C41A" size="22"></u-icon>
          <view class="pay-info">
            <text class="pay-name">微信支付</text>
            <text class="pay-tip">推荐使用</text>
          </view>
          <view class="pay-check" :class="{ checked: payMethod === 'wechat' }">
            <u-icon v-if="payMethod === 'wechat'" name="checkmark" color="#FFF" size="12"></u-icon>
          </view>
        </view>
        <view class="pay-item" @click="payMethod = 'balance'">
          <u-icon name="rmb-circle" color="#7B61FF" size="22"></u-icon>
          <view class="pay-info">
            <text class="pay-name">零钱支付</text>
            <text class="pay-tip">余额支付</text>
          </view>
          <view class="pay-check" :class="{ checked: payMethod === 'balance' }">
            <u-icon v-if="payMethod === 'balance'" name="checkmark" color="#FFF" size="12"></u-icon>
          </view>
        </view>
      </view>

      <view class="card remark-card" @click="editRemark">
        <text class="card-title">订单备注</text>
        <view class="remark-row">
          <u-icon name="edit-pen" color="#999" size="16"></u-icon>
          <text class="remark-text">{{ remark || '无备注' }}</text>
          <u-icon name="arrow-right" color="#CCC" size="14"></u-icon>
        </view>
      </view>
      <view class="bottom-spacer"></view>
    </scroll-view>

    <view class="submit-bar">
      <view class="submit-total">
        <text class="submit-label">应付</text>
        <text class="submit-amount">¥{{ total }}</text>
      </view>
      <view class="submit-btn" @click="submit">提交订单</view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getCartList, getTableList, submitOrder, payOrder } from '@/api/index.js'
import { showToast, showLoading, hideLoading } from '@/utils/common.js'

const loading = ref(false)
const cart = ref({ items: [] })
const store = ref({})
const payMethod = ref('wechat')
const remark = ref('')

const subtotal = computed(() => cart.value.items?.reduce((s, i) => s + i.price * i.quantity, 0) || 0)
const total = computed(() => subtotal.value + 2)

async function loadData() {
  loading.value = true
  try {
    const [cartData, tables] = await Promise.all([getCartList(), getTableList()])
    cart.value = cartData || { items: [] }
    store.value = tables?.[0] || {}
  } finally { loading.value = false }
}

function editRemark() {
  uni.showModal({
    title: '订单备注',
    editable: true,
    placeholderText: '请输入备注',
    success: (res) => { if (res.confirm) remark.value = res.content || '' }
  })
}

async function submit() {
  if (!cart.value.items?.length) { showToast('购物车为空'); return }
  showLoading('提交中...')
  try {
    const { orderId } = await submitOrder({ payMethod: payMethod.value, remark: remark.value })
    await payOrder({ orderId })
    hideLoading()
    showToast('下单成功')
    uni.redirectTo({ url: `/pages/order/detail?id=${orderId}` })
  } catch (e) { hideLoading() }
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.page { min-height: 100vh; background: #F8F9FB; }
.content { height: calc(100vh - 180rpx); padding: 24rpx 32rpx; }
.card { background: #FFF; border-radius: 32rpx; padding: 32rpx; margin-bottom: 24rpx; box-shadow: var(--shadow-card); }
.card-title { font-size: 30rpx; font-weight: 600; color: #1A1A1A; display: block; margin-bottom: 20rpx; }
.store-card {
  .store-name { font-size: 32rpx; font-weight: 700; color: #333; }
  .store-addr { font-size: 26rpx; color: #999; margin-top: 8rpx; display: block; }
  .dine-tag { display: inline-block; background: #EBE4FF; color: #7B61FF; font-size: 22rpx; padding: 4rpx 16rpx; border-radius: 20rpx; margin-top: 16rpx; }
  .seat-row { display: flex; justify-content: space-between; margin-top: 24rpx; padding-top: 24rpx; border-top: 1rpx dashed #EEE; }
  .seat-label { font-size: 28rpx; color: #666; }
  .seat-no { font-size: 32rpx; font-weight: 700; color: #7B61FF; }
}
.item-row { display: flex; align-items: center; margin-bottom: 20rpx; }
.item-img { width: 100rpx; height: 100rpx; border-radius: 16rpx; }
.item-info { flex: 1; margin-left: 16rpx; }
.item-name { font-size: 28rpx; display: block; }
.item-spec { font-size: 24rpx; color: #999; }
.item-right { text-align: right; font-size: 28rpx; }
.qty { display: block; font-size: 24rpx; color: #999; }
.fee-row { display: flex; justify-content: space-between; font-size: 26rpx; color: #666; padding: 8rpx 0; }
.total-row { display: flex; justify-content: space-between; margin-top: 16rpx; padding-top: 16rpx; border-top: 1rpx solid #F0F0F0; }
.total { font-size: 40rpx; font-weight: 700; color: #7B61FF; }
.pay-item { display: flex; align-items: center; padding: 20rpx 0; border-bottom: 1rpx solid #F0F0F0;
  &:last-child { border-bottom: none; }
  .pay-info { flex: 1; margin-left: 16rpx; }
  .pay-name { font-size: 28rpx; display: block; }
  .pay-tip { font-size: 24rpx; color: #999; }
  .pay-check { width: 40rpx; height: 40rpx; border-radius: 50%; border: 2rpx solid #DDD;
    &.checked { background: #7B61FF; border-color: #7B61FF; display: flex; align-items: center; justify-content: center; }
  }
}
.remark-row { display: flex; align-items: center; gap: 12rpx; }
.remark-text { flex: 1; font-size: 28rpx; color: #999; }
.bottom-spacer { height: 140rpx; }
.submit-bar {
  position: fixed; left: 0; right: 0; bottom: 0; background: #FFF; padding: 20rpx 32rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom)); display: flex; align-items: center;
  box-shadow: var(--shadow-bottom-bar);
  .submit-label { font-size: 24rpx; color: #999; }
  .submit-amount { font-size: 40rpx; font-weight: 700; color: #7B61FF; display: block; }
  .submit-btn { margin-left: auto; background: #7B61FF; color: #FFF; padding: 20rpx 60rpx; border-radius: 200rpx; font-size: 30rpx; font-weight: 600; }
}
</style>

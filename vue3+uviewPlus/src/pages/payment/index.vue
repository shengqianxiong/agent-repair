<template>
  <view class="page">
    <view class="amount-card">
      <text class="label">支付金额</text>
      <text class="amount">¥{{ formatPrice(amount) }}</text>
    </view>

    <u-cell-group>
      <u-cell title="微信支付" icon="weixin-fill" :value="payMethod" />
    </u-cell-group>

    <view class="bottom-bar">
      <u-button type="primary" text="确认支付" :loading="paying" @click="doPay" />
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { payOrder } from '@/api/order'
import { formatPrice } from '@/utils/navigate'

const orderId = ref('')
const amount = ref(0)
const paying = ref(false)
const payMethod = '微信安全支付'

onLoad((options) => {
  orderId.value = options.orderId || ''
  amount.value = Number(options.amount || 0)
})

async function doPay() {
  paying.value = true
  try {
    const payData = await payOrder({ orderId: orderId.value })
    if (payData && payData.timeStamp) {
      await new Promise((resolve, reject) => {
        uni.requestPayment({
          provider: 'wxpay',
          timeStamp: payData.timeStamp,
          nonceStr: payData.nonceStr,
          package: payData.package,
          signType: payData.signType || 'RSA',
          paySign: payData.paySign,
          success: resolve,
          fail: reject
        })
      })
    }
    uni.showToast({ title: '支付成功', icon: 'success' })
    setTimeout(() => {
      uni.redirectTo({ url: `/pages/order/detail?id=${orderId.value}` })
    }, 1500)
  } catch (e) {
    uni.showToast({ title: e.msg || '支付失败', icon: 'none' })
  } finally {
    paying.value = false
  }
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #0f0f1a;
  padding: 24rpx;
}
.amount-card {
  background: linear-gradient(135deg, #2d1b69, #1a1a2e);
  border-radius: 16rpx;
  padding: 48rpx;
  text-align: center;
  margin-bottom: 32rpx;
}
.label {
  font-size: 28rpx;
  color: #a0a0b8;
  display: block;
}
.amount {
  font-size: 64rpx;
  font-weight: 700;
  color: #f59e0b;
  margin-top: 16rpx;
  display: block;
}
.bottom-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 20rpx 24rpx;
  background: #1a1a2e;
}
</style>

<template>
  <view class="page page-dark">
    <scroll-view scroll-y class="page-scroll">
    <view class="amount-card">
      <text class="label">支付金额</text>
      <text class="amount">¥{{ formatPrice(amount) }}</text>
    </view>

    <u-cell-group title="支付方式">
      <u-cell title="微信支付" icon="weixin-fill" :value="'已选择'" isLink></u-cell>
    </u-cell-group>
    </scroll-view>

    <view class="footer page-footer">
      <u-button type="primary" text="确认支付" :loading="paying" @click="doPay"></u-button>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { payOrder } from '@/api/index.js'
import { formatPrice } from '@/utils/common.js'

const orderId = ref('')
const amount = ref(0)
const paying = ref(false)

async function doPay() {
  paying.value = true
  try {
    const payParams = await payOrder({ orderId: orderId.value })
    await new Promise((resolve, reject) => {
      uni.requestPayment({
        provider: 'wxpay',
        timeStamp: payParams.timeStamp,
        nonceStr: payParams.nonceStr,
        package: payParams.package,
        signType: payParams.signType || 'RSA',
        paySign: payParams.paySign,
        success: resolve,
        fail: reject
      })
    })
    uni.showToast({ title: '支付成功', icon: 'success' })
    setTimeout(() => {
      uni.redirectTo({ url: `/pages/order/detail?id=${orderId.value}` })
    }, 1500)
  } catch (e) {
    uni.showToast({ title: '支付失败', icon: 'none' })
  } finally {
    paying.value = false
  }
}

onMounted(() => {
  const pages = getCurrentPages()
  const cur = pages[pages.length - 1]
  const opts = cur.options || cur.$page?.options || {}
  orderId.value = opts.orderId || ''
  amount.value = opts.amount || 0
})
</script>

<style lang="scss" scoped>
.amount-card {
  margin: 48rpx 32rpx;
  padding: 48rpx;
  background: linear-gradient(135deg, #2d1b69, #1a1a2e);
  border-radius: 20rpx;
  text-align: center;
}
.label { color: #a0a0b8; display: block; margin-bottom: 16rpx; }
.amount { font-size: 64rpx; font-weight: 700; color: #f59e0b; }
.footer {
  padding: 24rpx 32rpx;
  padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
}
</style>

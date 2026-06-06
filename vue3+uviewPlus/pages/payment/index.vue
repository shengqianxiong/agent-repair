<template>
  <view class="bar-page payment-page">
    <view class="bar-card amount-card">
      <text class="label">支付金额</text>
      <text class="amount price-text">¥{{ formatPrice(amount) }}</text>
    </view>

    <view class="bar-card">
      <u-cell title="支付方式" value="微信支付" icon="weixin-fill" />
    </view>

    <view class="bottom-bar safe-bottom">
      <u-button type="primary" text="确认支付" :loading="paying" @click="doPay" />
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { payOrder } from '@/api/order'
import { formatPrice } from '@/utils/request'

const orderId = ref('')
const amount = ref(0)
const paying = ref(false)

async function doPay() {
  paying.value = true
  try {
    const payData = await payOrder({ orderId: orderId.value })
    if (payData?.mockPaid || payData?.paySuccess) {
      uni.showToast({ title: '支付成功', icon: 'success' })
      setTimeout(() => {
        uni.redirectTo({ url: `/pages/order/detail?id=${orderId.value}` })
      }, 1500)
      return
    }
    if (payData?.timeStamp) {
      uni.requestPayment({
        provider: 'wxpay',
        timeStamp: payData.timeStamp,
        nonceStr: payData.nonceStr,
        package: payData.packageValue || payData.package,
        signType: payData.signType || 'MD5',
        paySign: payData.paySign,
        success() {
          uni.redirectTo({ url: `/pages/order/detail?id=${orderId.value}` })
        },
        fail() {
          uni.showToast({ title: '支付取消', icon: 'none' })
        }
      })
    }
  } finally {
    paying.value = false
  }
}

onLoad((opts) => {
  orderId.value = opts.orderId
  amount.value = opts.amount
})
</script>

<style lang="scss" scoped>
.payment-page {
  padding-top: 40rpx;
}

.amount-card {
  text-align: center;
  padding: 60rpx 24rpx;

  .label {
    display: block;
    color: #999;
    font-size: 28rpx;
    margin-bottom: 16rpx;
  }

  .amount {
    font-size: 64rpx;
    font-weight: bold;
  }
}

.bottom-bar {
  position: fixed;
  left: 24rpx;
  right: 24rpx;
  bottom: 40rpx;
}
</style>

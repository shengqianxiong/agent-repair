<template>
  <view class="page">
    <view class="scan-section">
      <u-button type="primary" icon="scan" text="扫码核销" @click="scanCode" />
    </view>

    <view class="input-section">
      <u-input v-model="couponCode" placeholder="手动输入券码" border="surround" />
      <u-button type="warning" text="核销" :loading="verifying" @click="doVerify" />
    </view>

    <u-alert
      v-if="result"
      :title="result.success ? '核销成功' : '核销失败'"
      :type="result.success ? 'success' : 'error'"
      :description="result.message"
      showIcon
    />

    <view class="section-title">我的优惠券</view>
    <view v-if="coupons.length">
      <u-cell
        v-for="item in coupons"
        :key="item.id"
        :title="item.name"
        :label="item.code"
        :value="item.status"
      />
    </view>
    <u-empty v-else mode="coupon" text="暂无优惠券" />
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { verifyCoupon, getCouponList } from '@/api/coupon'

const couponCode = ref('')
const verifying = ref(false)
const result = ref(null)
const coupons = ref([])

onShow(() => loadCoupons())

async function loadCoupons() {
  try {
    const data = await getCouponList({ page: 1, pageSize: 20 })
    coupons.value = data?.list || []
  } catch {
    coupons.value = []
  }
}

function scanCode() {
  uni.scanCode({
    success: (res) => {
      couponCode.value = res.result
      doVerify()
    },
    fail: () => {
      uni.showToast({ title: '扫码取消', icon: 'none' })
    }
  })
}

async function doVerify() {
  if (!couponCode.value.trim()) {
    uni.showToast({ title: '请输入券码', icon: 'none' })
    return
  }
  verifying.value = true
  result.value = null
  try {
    const data = await verifyCoupon({ code: couponCode.value.trim() })
    result.value = { success: true, message: data?.message || `抵扣 ¥${data?.discountAmount || 0}` }
    loadCoupons()
  } catch (e) {
    result.value = { success: false, message: e.msg || '券码无效或已过期' }
  } finally {
    verifying.value = false
  }
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #0f0f1a;
  padding: 24rpx;
}
.scan-section {
  text-align: center;
  padding: 48rpx 0;
}
.input-section {
  display: flex;
  gap: 16rpx;
  margin-bottom: 32rpx;
}
.section-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #fff;
  margin: 32rpx 0 16rpx;
}
</style>

<template>
  <view class="page">
    <view class="scan-section">
      <u-button type="primary" icon="scan" text="扫码核销" @click="scanCode"></u-button>
    </view>

    <view class="input-section">
      <u-input v-model="couponCode" placeholder="手动输入券码" border="surround"></u-input>
      <u-button type="primary" text="核销" :loading="verifying" @click="verify"></u-button>
    </view>

    <u-alert
      v-if="result"
      :title="result.success ? '核销成功' : '核销失败'"
      :type="result.success ? 'success' : 'error'"
      :description="result.message"
      showIcon
    ></u-alert>

    <view class="coupon-list" v-if="coupons.length">
      <view class="section-title">我的优惠券</view>
      <u-cell
        v-for="item in coupons"
        :key="item.id"
        :title="item.name"
        :label="item.code"
        :value="item.status"
      ></u-cell>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { verifyCoupon, getCouponList } from '@/api/index.js'

const couponCode = ref('')
const verifying = ref(false)
const result = ref(null)
const coupons = ref([])

async function loadCoupons() {
  try {
    const data = await getCouponList({ page: 1, pageSize: 20 })
    coupons.value = data?.list || data || []
  } catch (e) {}
}

function scanCode() {
  uni.scanCode({
    success: (res) => {
      couponCode.value = res.result
      verify()
    }
  })
}

async function verify() {
  if (!couponCode.value.trim()) {
    uni.showToast({ title: '请输入券码', icon: 'none' })
    return
  }
  verifying.value = true
  try {
    const data = await verifyCoupon({ code: couponCode.value.trim() })
    result.value = { success: true, message: data?.message || '核销成功，可在下单时使用' }
    loadCoupons()
  } catch (e) {
    result.value = { success: false, message: e?.msg || '券码无效或已过期' }
  } finally {
    verifying.value = false
  }
}

onMounted(loadCoupons)
</script>

<style lang="scss" scoped>
.page { min-height: 100vh; background: #0f0f1a; padding: 32rpx; }
.scan-section { margin-bottom: 32rpx; }
.input-section {
  display: flex;
  gap: 16rpx;
  margin-bottom: 32rpx;
}
.section-title { color: #a0a0b8; margin: 32rpx 0 16rpx; }
</style>

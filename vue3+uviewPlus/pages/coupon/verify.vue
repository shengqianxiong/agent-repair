<template>
  <view class="bar-page">
    <view class="bar-card">
      <u-button type="primary" icon="scan" text="扫码核销" @click="scanCode" />
    </view>

    <view class="bar-card">
      <view class="section-title">手动输入券码</view>
      <u-input v-model="couponCode" placeholder="请输入团购券码" border="surround" />
      <u-button type="primary" text="核销" :loading="verifying" customStyle="margin-top: 24rpx" @click="verify" />
    </view>

    <view class="bar-card" v-if="result">
      <u-alert :title="result.success ? '核销成功' : '核销失败'" :type="result.success ? 'success' : 'error'" :description="result.message" />
    </view>

    <view class="bar-card" v-if="coupons.length">
      <view class="section-title">我的优惠券</view>
      <u-cell v-for="c in coupons" :key="c.id" :title="c.name" :value="c.status" :label="c.code" />
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { verifyCoupon, getCouponList } from '@/api/marketing'

const couponCode = ref('')
const verifying = ref(false)
const result = ref(null)
const coupons = ref([])

async function loadCoupons() {
  try {
    const res = await getCouponList({ page: 1, pageSize: 20 })
    coupons.value = res?.list || res || []
  } catch (e) {}
}

function scanCode() {
  uni.scanCode({
    success(res) {
      couponCode.value = res.result
      verify()
    },
    fail() {
      uni.showToast({ title: '扫码取消', icon: 'none' })
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
    result.value = { success: true, message: data?.message || `抵扣 ¥${data?.discountAmount || 0}` }
    loadCoupons()
  } catch (e) {
    result.value = { success: false, message: e?.msg || '券码无效或已核销' }
  } finally {
    verifying.value = false
  }
}

onShow(() => loadCoupons())
</script>

<style lang="scss" scoped>
.section-title {
  font-weight: bold;
  margin-bottom: 16rpx;
}
</style>

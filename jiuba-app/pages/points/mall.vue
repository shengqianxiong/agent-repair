<template>
  <view class="page">
    <LoadingOverlay :show="loading" />
    <AppNavbar title="积分商城" right-icon="clock" bg-color="#F8F9FB" />

    <scroll-view scroll-y class="content">
      <view class="balance-card">
        <text class="balance-label">当前可用积分</text>
        <text class="balance-value">{{ balance }}</text>
        <view class="balance-links">
          <text @click="showToast('积分明细')">积分明细</text>
          <text @click="showToast('我的兑换')">我的兑换</text>
        </view>
      </view>

      <scroll-view scroll-x class="category-tabs">
        <view
          v-for="cat in categories"
          :key="cat"
          class="cat-tab"
          :class="{ active: activeCat === cat }"
          @click="activeCat = cat"
        >{{ cat }}</view>
      </scroll-view>

      <view class="section-header">
        <text class="section-title">热门兑换</text>
        <u-icon name="hourglass" color="#999" size="18"></u-icon>
      </view>

      <view class="product-grid">
        <view v-for="p in filteredProducts" :key="p.id" class="product-card">
          <view class="product-img-wrap">
            <image class="product-img" :src="p.image" mode="aspectFill" />
            <view v-if="p.tag" class="product-tag" :class="p.tag.toLowerCase()">{{ p.tag }}</view>
          </view>
          <text class="product-name">{{ p.name }}</text>
          <text class="product-points">{{ p.points }} 积分</text>
          <view class="redeem-btn" @click="redeem(p)">立即兑换</view>
        </view>
      </view>

      <view class="earn-banner">
        <view class="earn-text">
          <text class="earn-title">赚取更多积分</text>
          <text class="earn-desc">每日签到可获得积分奖励</text>
        </view>
        <view class="checkin-btn" @click="checkin">去签到</view>
      </view>
    </scroll-view>
    <AppTabbar current="home" />
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getPointsBalance, getMallProductList, redeemPoints, checkinPoints } from '@/api/index.js'
import { showToast, showLoading, hideLoading } from '@/utils/common.js'

const loading = ref(false)
const balance = ref(0)
const products = ref([])
const activeCat = ref('全部商品')

const categories = ['全部商品', '数码周边', '虚拟礼包', '生活']

const filteredProducts = computed(() => {
  if (activeCat.value === '全部商品') return products.value
  return products.value.filter((p) => p.category === activeCat.value)
})

async function loadData() {
  loading.value = true
  try {
    const [bal, list] = await Promise.all([getPointsBalance(), getMallProductList()])
    balance.value = bal?.balance ?? 0
    products.value = list?.list || []
  } finally { loading.value = false }
}

async function redeem(p) {
  if (balance.value < p.points) { showToast('积分不足'); return }
  showLoading()
  try { await redeemPoints({ productId: p.id }); showToast('兑换成功'); loadData() } finally { hideLoading() }
}

async function checkin() {
  showLoading()
  try {
    const res = await checkinPoints()
    showToast(`签到成功 +${res?.points || 10}积分`)
    loadData()
  } finally { hideLoading() }
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.page { min-height: 100vh; background: #F8F9FB; padding-bottom: 120rpx; }
.content { padding: 24rpx 32rpx; }
.balance-card {
  background: linear-gradient(135deg, #7246F2, #9D7BFF); border-radius: 64rpx; padding: 40rpx; color: #FFF; margin-bottom: 24rpx;
  .balance-label { font-size: 28rpx; opacity: 0.9; display: block; }
  .balance-value { font-size: 64rpx; font-weight: 700; display: block; margin: 12rpx 0 20rpx; }
  .balance-links { display: flex; gap: 32rpx; font-size: 26rpx; opacity: 0.9; }
}
.category-tabs { white-space: nowrap; margin-bottom: 24rpx; }
.cat-tab {
  display: inline-block; padding: 12rpx 28rpx; margin-right: 16rpx; border-radius: 200rpx; font-size: 26rpx;
  background: rgba(123,97,255,0.15); color: #7B61FF;
  &.active { background: #FFF; color: #7B61FF; font-weight: 600; }
}
.section-header { display: flex; justify-content: space-between; margin-bottom: 20rpx; }
.section-title { font-size: 32rpx; font-weight: 700; }
.product-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 20rpx; margin-bottom: 32rpx; }
.product-card {
  background: #FFF; border-radius: 48rpx; padding: 20rpx; box-shadow: var(--shadow-card);
  .product-img-wrap { position: relative; border-radius: 32rpx; overflow: hidden; margin-bottom: 12rpx; }
  .product-img { width: 100%; height: 240rpx; }
  .product-tag { position: absolute; top: 12rpx; left: 12rpx; font-size: 20rpx; padding: 4rpx 12rpx; border-radius: 12rpx; color: #FFF;
    &.hot { background: #FF4D4F; }
    &.limited { background: #FAAD14; }
  }
  .product-name { font-size: 28rpx; font-weight: 600; display: block; }
  .product-points { font-size: 26rpx; color: #7B61FF; margin: 8rpx 0 16rpx; display: block; }
  .redeem-btn { background: #7B61FF; color: #FFF; text-align: center; padding: 12rpx; border-radius: 200rpx; font-size: 24rpx; }
}
.earn-banner {
  display: flex; align-items: center; justify-content: space-between;
  background: #F2F1F0; border-radius: 64rpx; padding: 32rpx; margin-bottom: 32rpx;
  .earn-title { font-size: 30rpx; font-weight: 600; display: block; }
  .earn-desc { font-size: 24rpx; color: #999; margin-top: 8rpx; display: block; }
  .checkin-btn { background: #7B61FF; color: #FFF; padding: 12rpx 28rpx; border-radius: 200rpx; font-size: 26rpx; }
}
</style>

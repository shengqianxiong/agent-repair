<template>
  <view class="page" v-if="product">
    <u-swiper
      :list="swiperList"
      keyName="url"
      height="500rpx"
      indicator
    />
    <view class="info-card">
      <text class="name">{{ product.name }}</text>
      <view class="price-row">
        <text class="price">¥{{ formatPrice(product.price) }}</text>
        <text v-if="product.originalPrice" class="original">¥{{ formatPrice(product.originalPrice) }}</text>
      </view>
      <text class="desc">{{ product.description || '暂无描述' }}</text>
      <view class="stock">库存：{{ product.stock ?? 0 }}</view>
    </view>

    <view class="qty-row">
      <text>数量</text>
      <u-number-box v-model="quantity" :min="1" :max="product.stock || 99" />
    </view>

    <view class="bottom-bar">
      <u-button type="warning" text="加入购物车" @click="addToCart" />
      <u-button type="primary" text="立即购买" @click="buyNow" />
    </view>
  </view>
  <u-loading-page :loading="loading" />
</template>

<script setup>
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getProductDetail } from '@/api/product'
import { addCart as addCartApi } from '@/api/cart'
import { addToLocalCart } from '@/utils/cart'
import { formatPrice } from '@/utils/navigate'

const product = ref(null)
const quantity = ref(1)
const loading = ref(true)
let productId = null

const swiperList = computed(() => {
  if (!product.value?.image) return [{ url: '/static/default-product.png' }]
  return [{ url: product.value.image }]
})

onLoad((options) => {
  productId = options.id
  loadDetail()
})

async function loadDetail() {
  loading.value = true
  try {
    product.value = await getProductDetail(productId)
  } catch (e) {
    uni.showToast({ title: e.msg || '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

async function addToCart() {
  try {
    await addCartApi({ productId: product.value.id, quantity: quantity.value })
  } catch { /* */ }
  addToLocalCart(product.value, quantity.value)
  uni.showToast({ title: '已加入购物车', icon: 'success' })
}

function buyNow() {
  addToLocalCart(product.value, quantity.value)
  uni.navigateTo({ url: '/pages/cart/index' })
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #0f0f1a;
  padding-bottom: 140rpx;
}
.info-card {
  padding: 24rpx;
  background: #1a1a2e;
  margin: 16rpx;
  border-radius: 12rpx;
}
.name {
  font-size: 36rpx;
  font-weight: 600;
  color: #fff;
  display: block;
}
.price-row {
  margin: 16rpx 0;
  display: flex;
  align-items: baseline;
  gap: 12rpx;
}
.price {
  font-size: 40rpx;
  color: #f59e0b;
  font-weight: 700;
}
.original {
  font-size: 26rpx;
  color: #6b6b80;
  text-decoration: line-through;
}
.desc {
  font-size: 28rpx;
  color: #a0a0b8;
  line-height: 1.6;
}
.stock {
  margin-top: 12rpx;
  font-size: 24rpx;
  color: #6b6b80;
}
.qty-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx 32rpx;
  background: #1a1a2e;
  margin: 0 16rpx;
  border-radius: 12rpx;
  color: #fff;
}
.bottom-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  gap: 16rpx;
  padding: 20rpx 24rpx;
  background: #1a1a2e;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
}
</style>

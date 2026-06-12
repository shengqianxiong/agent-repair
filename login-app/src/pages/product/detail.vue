<template>
  <view class="page" v-if="product">
    <scroll-view scroll-y class="page-body">
    <u-swiper
      :list="swiperList"
      keyName="image"
      height="600rpx"
      indicator
      circular
    ></u-swiper>
    <view class="info">
      <text class="name">{{ product.name }}</text>
      <view class="price-row">
        <text class="price">¥{{ formatPrice(product.price) }}</text>
        <text v-if="product.originalPrice" class="original">¥{{ formatPrice(product.originalPrice) }}</text>
      </view>
      <text class="desc">{{ product.description || '暂无描述' }}</text>
      <view class="stock">库存：{{ product.stock ?? '-' }}</view>
    </view>
    <view class="quantity-row">
      <text>数量</text>
      <u-number-box v-model="quantity" :min="1" :max="product.stock || 99"></u-number-box>
    </view>
    </scroll-view>
    <view class="footer">
      <u-button type="info" text="加入购物车" @click="addCart"></u-button>
      <u-button type="primary" text="立即购买" @click="buyNow"></u-button>
    </view>
  </view>
  <u-loading-page v-else loading></u-loading-page>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getProductDetail, addToCart } from '@/api/index.js'
import { formatPrice } from '@/utils/common.js'

const product = ref(null)
const quantity = ref(1)

const swiperList = computed(() => {
  const img = product.value?.image || 'https://cdn.uviewui.com/uview/album/1.jpg'
  return [{ image: img }]
})

async function loadDetail(id) {
  try {
    product.value = await getProductDetail(id)
  } catch (e) {
    uni.navigateBack()
  }
}

async function addCart() {
  try {
    await addToCart({ productId: product.value.id, quantity: quantity.value })
    uni.showToast({ title: '已加入购物车', icon: 'success' })
  } catch (e) {}
}

async function buyNow() {
  try {
    await addToCart({ productId: product.value.id, quantity: quantity.value })
    uni.navigateTo({ url: '/pages/order/confirm' })
  } catch (e) {}
}

onMounted(() => {
  const pages = getCurrentPages()
  const cur = pages[pages.length - 1]
  const id = cur.options?.id || cur.$page?.options?.id
  if (id) loadDetail(id)
})
</script>

<style lang="scss" scoped>
.page { background: #0f0f1a; }
.info { padding: 32rpx; }
.name { font-size: 36rpx; font-weight: 700; color: #fff; }
.price-row { margin: 16rpx 0; display: flex; align-items: baseline; gap: 16rpx; }
.price { font-size: 40rpx; color: #f59e0b; font-weight: 700; }
.original { font-size: 26rpx; color: #6b6b80; text-decoration: line-through; }
.desc { color: #a0a0b8; line-height: 1.6; display: block; margin-top: 16rpx; }
.stock { margin-top: 16rpx; color: #6b6b80; font-size: 24rpx; }
.quantity-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32rpx;
  background: #1a1a2e;
  margin: 0 24rpx;
  border-radius: 12rpx;
}
.footer {
  flex-shrink: 0;
  display: flex;
  gap: 20rpx;
  padding: 24rpx 32rpx;
  background: #1a1a2e;
  padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
}
</style>

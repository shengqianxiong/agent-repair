<template>
  <view class="bar-page" v-if="product">
    <u-swiper v-if="images.length" :list="images" height="400" indicator />

    <view class="bar-card">
      <text class="name">{{ product.name }}</text>
      <view class="price-row">
        <text class="price-text">¥{{ formatPrice(product.price) }}</text>
        <text v-if="product.originalPrice" class="original">¥{{ formatPrice(product.originalPrice) }}</text>
      </view>
      <text class="stock">库存：{{ product.stock ?? '充足' }}</text>
      <text class="desc">{{ product.description || '暂无描述' }}</text>
    </view>

    <view class="bar-card">
      <view class="qty-row">
        <text>数量</text>
        <u-number-box v-model="quantity" :min="1" :max="product.stock || 99" />
      </view>
    </view>

    <view class="bottom-bar safe-bottom">
      <u-button type="warning" text="加入购物车" @click="addCart" />
      <u-button type="primary" text="立即购买" @click="buyNow" />
    </view>
  </view>
  <u-loading-page v-else loading />
</template>

<script setup>
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getProductDetail, getPackageDetail, addToCart } from '@/api/order'
import { formatPrice } from '@/utils/request'

const product = ref(null)
const quantity = ref(1)
const productId = ref(null)
const pageType = ref('product')

const images = computed(() => {
  if (!product.value?.image) return []
  return [{ url: product.value.image }]
})

async function loadDetail() {
  if (pageType.value === 'package') {
    product.value = await getPackageDetail(productId.value)
  } else {
    product.value = await getProductDetail(productId.value)
  }
}

async function addCart() {
  await addToCart({ productId: productId.value, quantity: quantity.value })
  uni.showToast({ title: '已加入购物车', icon: 'success' })
}

function buyNow() {
  addToCart({ productId: productId.value, quantity: quantity.value }).then(() => {
    uni.navigateTo({ url: '/pages/cart/index' })
  })
}

onLoad((opts) => {
  productId.value = opts.id
  pageType.value = opts.type || 'product'
  loadDetail()
})
</script>

<style lang="scss" scoped>
.name {
  font-size: 36rpx;
  font-weight: bold;
  display: block;
  margin-bottom: 16rpx;
}

.price-row {
  display: flex;
  align-items: baseline;
  gap: 16rpx;
  margin-bottom: 12rpx;
}

.original {
  color: #999;
  font-size: 24rpx;
  text-decoration: line-through;
}

.stock, .desc {
  display: block;
  font-size: 26rpx;
  color: #666;
  margin-top: 12rpx;
  line-height: 1.6;
}

.qty-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.bottom-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  background: #fff;
  padding: 20rpx 24rpx;
  display: flex;
  gap: 20rpx;
  box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.06);
}
</style>

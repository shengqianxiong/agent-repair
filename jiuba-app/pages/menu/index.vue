<template>
  <view class="page">
    <LoadingOverlay :show="loading" />
    <view class="navbar">
      <view class="nav-left"><u-icon name="list" color="#7B61FF" size="22"></u-icon></view>
      <text class="nav-title">云享生活</text>
      <view class="nav-right">
        <u-icon name="search" color="#7B61FF" size="20" style="margin-right:16rpx"></u-icon>
        <u-icon name="bell" color="#7B61FF" size="20"></u-icon>
      </view>
    </view>

    <view class="menu-body">
      <scroll-view scroll-y class="category-side">
        <view
          v-for="cat in categories"
          :key="cat.id"
          class="cat-item"
          :class="{ active: activeCat === cat.id }"
          @click="switchCat(cat.id)"
        >
          <view v-if="activeCat === cat.id" class="cat-indicator"></view>
          <text>{{ cat.name }}</text>
        </view>
      </scroll-view>

      <scroll-view scroll-y class="product-area">
        <view class="product-header">
          <text class="cat-title">{{ currentCatName }}系列</text>
          <text class="cat-desc">精选优质酒水，新鲜现做</text>
        </view>
        <view v-if="products.length === 0" class="empty-wrap"><EmptyState text="该分类暂无商品" /></view>
        <view v-for="p in products" :key="p.id" class="product-card" @click="goDetail(p.id)">
          <image class="product-img" :src="p.image" mode="aspectFill" />
          <view class="product-info">
            <text class="product-name">{{ p.name }}</text>
            <text class="product-desc">{{ p.desc }}</text>
            <view class="product-bottom">
              <view class="price-row">
                <text class="price">¥{{ p.price }}</text>
                <view v-if="p.memberPrice" class="member-tag">会员¥{{ p.memberPrice }}</view>
              </view>
              <view class="add-btn" @click.stop="addCart(p)"><u-icon name="plus" color="#FFF" size="14"></u-icon></view>
            </view>
          </view>
        </view>
      </scroll-view>
    </view>

    <view v-if="cart.count > 0" class="checkout-bar">
      <view class="cart-icon-wrap">
        <u-icon name="shopping-cart" color="#7B61FF" size="24"></u-icon>
        <view class="cart-badge">{{ cart.count }}</view>
      </view>
      <view class="checkout-info">
        <text class="total">¥{{ cart.totalAmount?.toFixed(2) }}</text>
        <text class="discount">预估优惠 ¥{{ cart.discountAmount || 0 }}</text>
      </view>
      <view class="checkout-btn" @click="goConfirm">去结算</view>
    </view>

    <AppTabbar current="menu" />
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getCategoryList, getProductList, getCartList, addToCart } from '@/api/index.js'
import { showToast } from '@/utils/common.js'

const loading = ref(false)
const categories = ref([])
const products = ref([])
const activeCat = ref(1)
const cart = ref({ count: 0, totalAmount: 0, discountAmount: 0 })

const currentCatName = computed(() => categories.value.find((c) => c.id === activeCat.value)?.name || '精酿啤酒')

async function loadCategories() {
  categories.value = await getCategoryList()
  if (categories.value.length) activeCat.value = categories.value[0].id
}

async function loadProducts() {
  products.value = await getProductList({ categoryId: activeCat.value })
}

async function loadCart() {
  const data = await getCartList()
  cart.value = data || { count: 0, totalAmount: 0 }
}

async function switchCat(id) {
  activeCat.value = id
  loading.value = true
  try { await loadProducts() } finally { loading.value = false }
}

async function addCart(product) {
  await addToCart({ productId: product.id, quantity: 1 })
  showToast('已加入购物车')
  await loadCart()
}

function goConfirm() { uni.navigateTo({ url: '/pages/order/confirm' }) }
function goDetail(id) { showToast(`商品 ${id}`) }

onMounted(async () => {
  loading.value = true
  try {
    await loadCategories()
    await Promise.all([loadProducts(), loadCart()])
  } finally { loading.value = false }
})
</script>

<style lang="scss" scoped>
.page { min-height: 100vh; background: #FFFFFF; padding-bottom: 200rpx; }
.navbar {
  display: flex; align-items: center; height: 88rpx; padding: 0 32rpx; border-bottom: 1rpx solid #EEE;
  .nav-title { flex: 1; text-align: center; font-size: 36rpx; font-weight: 700; color: #7B61FF; }
  .nav-left, .nav-right { width: 120rpx; display: flex; align-items: center; }
  .nav-right { justify-content: flex-end; }
}
.menu-body { display: flex; height: calc(100vh - 288rpx); }
.category-side {
  width: 200rpx; background: #F8F8F8;
  .cat-item {
    position: relative; padding: 32rpx 16rpx; text-align: center; font-size: 26rpx; color: #666;
    &.active { color: #7B61FF; font-weight: 600; background: #FFFFFF; }
    .cat-indicator { position: absolute; left: 0; top: 50%; transform: translateY(-50%); width: 6rpx; height: 40rpx; background: #7B61FF; border-radius: 0 6rpx 6rpx 0; }
  }
}
.product-area { flex: 1; padding: 0 24rpx; }
.product-header { padding: 30rpx 0 20rpx; }
.cat-title { font-size: 32rpx; font-weight: 700; color: #1A1A1A; display: block; }
.cat-desc { font-size: 24rpx; color: #999; margin-top: 8rpx; display: block; }
.product-card {
  display: flex; background: #FFFFFF; border-radius: 32rpx; padding: 20rpx; margin-bottom: 20rpx;
  box-shadow: var(--shadow-card);
  .product-img { width: 160rpx; height: 160rpx; border-radius: 32rpx; flex-shrink: 0; }
  .product-info { flex: 1; margin-left: 20rpx; display: flex; flex-direction: column; justify-content: space-between; }
  .product-name { font-size: 30rpx; font-weight: 600; color: #1A1A1A; }
  .product-desc { font-size: 24rpx; color: #999; margin-top: 8rpx; }
  .product-bottom { display: flex; justify-content: space-between; align-items: center; margin-top: 12rpx; }
  .price { font-size: 32rpx; font-weight: 700; color: #1A1A1A; }
  .member-tag { display: inline-block; background: #FFE4E1; color: #E64340; font-size: 20rpx; padding: 4rpx 12rpx; border-radius: 20rpx; margin-left: 8rpx; }
  .add-btn { width: 48rpx; height: 48rpx; border-radius: 50%; background: #7B61FF; display: flex; align-items: center; justify-content: center; }
}
.checkout-bar {
  position: fixed; left: 32rpx; right: 32rpx; bottom: 130rpx; z-index: 100;
  display: flex; align-items: center; background: #FFFFFF; border-radius: 60rpx;
  padding: 16rpx 24rpx; box-shadow: var(--shadow-float-bar);
  .cart-icon-wrap { position: relative; margin-right: 16rpx; }
  .cart-badge { position: absolute; top: -8rpx; right: -12rpx; background: #FF4D4F; color: #FFF; font-size: 20rpx; min-width: 32rpx; height: 32rpx; border-radius: 50%; display: flex; align-items: center; justify-content: center; }
  .checkout-info { flex: 1; }
  .total { font-size: 32rpx; font-weight: 700; color: #1A1A1A; display: block; }
  .discount { font-size: 22rpx; color: #999; }
  .checkout-btn { background: #7B61FF; color: #FFF; padding: 16rpx 40rpx; border-radius: 200rpx; font-size: 28rpx; font-weight: 600; }
}
</style>

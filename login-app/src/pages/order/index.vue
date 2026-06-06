<template>
  <view class="page">
    <u-navbar title="点餐" :autoBack="false" bgColor="#1a1a2e" titleStyle="color:#fff">
      <template #right>
        <view @click="goCart">
          <u-icon name="shopping-cart" color="#fff" size="22"></u-icon>
          <u-badge v-if="cartCount > 0" :value="cartCount" absolute :offset="[-4, -4]"></u-badge>
        </view>
      </template>
    </u-navbar>

    <view class="main">
      <scroll-view scroll-y class="category-panel">
        <view
          v-for="(cat, idx) in categories"
          :key="cat.id"
          class="category-item"
          :class="{ active: currentCategory === idx }"
          @click="switchCategory(idx, cat.id)"
        >
          {{ cat.name }}
        </view>
      </scroll-view>

      <scroll-view scroll-y class="product-panel" @scrolltolower="loadMore">
        <view v-if="products.length" class="product-list">
          <view v-for="item in products" :key="item.id" class="product-card" @click="goDetail(item.id)">
            <u-image :src="item.image || defaultImg" width="160rpx" height="160rpx" radius="8"></u-image>
            <view class="product-info">
              <text class="product-name">{{ item.name }}</text>
              <text class="product-desc">{{ item.description || '精选酒水' }}</text>
              <view class="product-bottom">
                <text class="price">¥{{ formatPrice(item.price) }}</text>
                <u-button type="primary" size="mini" text="加购" @click.stop="addCart(item)"></u-button>
              </view>
            </view>
          </view>
        </view>
        <u-empty v-else mode="list" text="暂无商品"></u-empty>
        <u-loadmore :status="loadStatus"></u-loadmore>
      </scroll-view>
    </view>

    <!-- 购物车底栏 -->
    <view class="cart-bar" @click="goCart">
      <view class="cart-left">
        <u-icon name="shopping-cart-fill" color="#7c3aed" size="28"></u-icon>
        <u-badge v-if="cartCount > 0" :value="cartCount" absolute :offset="[8, -8]"></u-badge>
        <text class="cart-total">合计 ¥{{ formatPrice(cartTotal) }}</text>
      </view>
      <u-button type="primary" text="去结算" size="small" @click.stop="goCart"></u-button>
    </view>

    <app-tabbar current="order"></app-tabbar>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onPullDownRefresh, onShow } from '@dcloudio/uni-app'
import { getCategoryList, getProductList, addToCart, getCartList } from '@/api/index.js'
import { formatPrice } from '@/utils/common.js'

const defaultImg = 'https://cdn.uviewui.com/uview/album/1.jpg'
const categories = ref([])
const currentCategory = ref(0)
const categoryId = ref(null)
const products = ref([])
const page = ref(1)
const loadStatus = ref('loadmore')
const cartCount = ref(0)
const cartTotal = ref(0)

async function loadCategories() {
  try {
    const data = await getCategoryList()
    categories.value = data || []
    if (categories.value.length) {
      categoryId.value = categories.value[0].id
    }
  } catch (e) {
    categories.value = []
  }
}

async function loadProducts(reset = false) {
  if (reset) {
    page.value = 1
    products.value = []
  }
  loadStatus.value = 'loading'
  try {
    const data = await getProductList({
      page: page.value,
      pageSize: 20,
      categoryId: categoryId.value
    })
    const items = data?.list || []
    products.value = reset ? items : [...products.value, ...items]
    loadStatus.value = items.length < 20 ? 'nomore' : 'loadmore'
  } catch (e) {
    loadStatus.value = 'loadmore'
  }
}

async function loadCart() {
  try {
    const data = await getCartList()
    const items = data?.items || data?.list || data || []
    cartCount.value = items.reduce((s, i) => s + (i.quantity || 0), 0)
    cartTotal.value = items.reduce((s, i) => s + (i.subtotal || i.price * i.quantity || 0), 0)
  } catch (e) {
    cartCount.value = 0
    cartTotal.value = 0
  }
}

function switchCategory(idx, id) {
  currentCategory.value = idx
  categoryId.value = id
  loadProducts(true)
}

async function addCart(item) {
  try {
    await addToCart({ productId: item.id, quantity: 1 })
    uni.showToast({ title: '已加入购物车', icon: 'success' })
    loadCart()
  } catch (e) {}
}

function goDetail(id) {
  uni.navigateTo({ url: `/pages/product/detail?id=${id}` })
}

function goCart() {
  uni.navigateTo({ url: '/pages/cart/index' })
}

function loadMore() {
  if (loadStatus.value === 'loadmore') {
    page.value++
    loadProducts()
  }
}

onMounted(async () => {
  await loadCategories()
  await loadProducts(true)
  await loadCart()
})
onShow(loadCart)
onPullDownRefresh(async () => {
  await loadCategories()
  await loadProducts(true)
  await loadCart()
  uni.stopPullDownRefresh()
})
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #0f0f1a;
  padding-bottom: 200rpx;
}
.main {
  display: flex;
  height: calc(100vh - 88rpx - 200rpx);
}
.category-panel {
  width: 180rpx;
  background: #1a1a2e;
}
.category-item {
  padding: 32rpx 16rpx;
  text-align: center;
  font-size: 26rpx;
  color: #a0a0b8;
  &.active {
    background: #0f0f1a;
    color: #7c3aed;
    font-weight: 600;
  }
}
.product-panel {
  flex: 1;
  padding: 16rpx;
}
.product-card {
  display: flex;
  gap: 20rpx;
  background: #1a1a2e;
  border-radius: 12rpx;
  padding: 20rpx;
  margin-bottom: 16rpx;
}
.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.product-name { font-size: 28rpx; font-weight: 600; color: #fff; }
.product-desc { font-size: 22rpx; color: #6b6b80; margin: 8rpx 0; }
.product-bottom { display: flex; justify-content: space-between; align-items: center; }
.price { color: #f59e0b; font-size: 32rpx; font-weight: 700; }
.cart-bar {
  position: fixed;
  left: 24rpx;
  right: 24rpx;
  bottom: 120rpx;
  background: #1a1a2e;
  border-radius: 48rpx;
  padding: 16rpx 24rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  z-index: 99;
}
.cart-left {
  position: relative;
  display: flex;
  align-items: center;
  gap: 16rpx;
}
.cart-total { color: #fff; font-weight: 600; }
</style>

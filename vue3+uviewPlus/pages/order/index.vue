<template>
  <view class="bar-page order-page">
    <u-navbar title="点餐" :autoBack="false" bgColor="#1a1a2e" titleColor="#fff" />

    <view class="order-body">
      <scroll-view scroll-y class="category-panel">
        <view
          v-for="(cat, idx) in categories"
          :key="cat.id"
          class="category-item"
          :class="{ active: currentIndex === idx }"
          @click="switchCategory(idx)"
        >
          {{ cat.name }}
        </view>
      </scroll-view>

      <scroll-view scroll-y class="product-panel" @scrolltolower="loadMore">
        <view v-if="!products.length && !loading" class="empty-wrap">
          <u-empty mode="list" text="暂无商品" />
        </view>
        <view v-for="item in products" :key="item.id" class="product-card" @click="goDetail(item.id)">
          <u-image :src="item.image" width="140rpx" height="140rpx" radius="8" />
          <view class="product-info">
            <text class="product-name">{{ item.name }}</text>
            <text class="product-desc u-line-2">{{ item.description }}</text>
            <view class="product-bottom">
              <text class="price-text">¥{{ formatPrice(item.price) }}</text>
              <u-button type="primary" size="mini" text="加购" @click.stop="addCart(item)" />
            </view>
          </view>
        </view>
        <u-loadmore v-if="products.length" :status="loadStatus" />
      </scroll-view>
    </view>

    <view class="cart-bar safe-bottom" @click="goCart">
      <view class="cart-left">
        <u-badge :value="cartCount" :max="99" absolute :offset="[-8, -8]">
          <u-icon name="shopping-cart-fill" color="#fff" size="28" />
        </u-badge>
        <text class="cart-total">¥{{ formatPrice(cartTotal) }}</text>
      </view>
      <u-button type="primary" text="去结算" :disabled="cartCount === 0" @click.stop="goCart" />
    </view>

    <BarTabbar current="order" />
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onShow, onPullDownRefresh } from '@dcloudio/uni-app'
import BarTabbar from '@/components/BarTabbar.vue'
import { getCategoryList, getProductList, getCartList, addToCart as addCartApi } from '@/api/order'
import { formatPrice } from '@/utils/request'
import { isLoggedIn, setToken } from '@/utils/auth'
import { login } from '@/api/home'

const categories = ref([{ id: null, name: '全部' }])
const currentIndex = ref(0)
const products = ref([])
const cartItems = ref([])
const loading = ref(false)
const loadStatus = ref('loadmore')
const page = ref(1)
const pageSize = 20
const total = ref(0)

const cartCount = computed(() => cartItems.value.reduce((s, i) => s + (i.quantity || 0), 0))
const cartTotal = computed(() => cartItems.value.reduce((s, i) => s + (i.subtotal || i.price * i.quantity || 0), 0))

async function ensureLogin() {
  if (isLoggedIn()) return
  const data = await login({ openid: 'test001', nickname: '酒友' })
  if (data?.token) setToken(data.token)
}

async function loadCategories() {
  const list = await getCategoryList()
  categories.value = [{ id: null, name: '全部' }, ...(list || [])]
}

async function loadProducts(reset = true) {
  if (reset) {
    page.value = 1
    products.value = []
  }
  loading.value = true
  loadStatus.value = 'loading'
  try {
    const cat = categories.value[currentIndex.value]
    const res = await getProductList({
      page: page.value,
      pageSize,
      categoryId: cat?.id || undefined,
      status: 1
    })
    const list = res?.list || []
    total.value = res?.total || 0
    products.value = reset ? list : [...products.value, ...list]
    loadStatus.value = products.value.length >= total.value ? 'nomore' : 'loadmore'
  } catch (e) {
    loadStatus.value = 'loadmore'
  } finally {
    loading.value = false
  }
}

async function loadCart() {
  try {
    const list = await getCartList()
    cartItems.value = Array.isArray(list) ? list : (list?.list || [])
  } catch (e) {
    cartItems.value = []
  }
}

function switchCategory(idx) {
  currentIndex.value = idx
  loadProducts(true)
}

function loadMore() {
  if (loadStatus.value !== 'loadmore') return
  page.value++
  loadProducts(false)
}

async function addCart(item) {
  try {
    await addCartApi({ productId: item.id, quantity: 1 })
    uni.showToast({ title: '已加入购物车', icon: 'success' })
    loadCart()
  } catch (e) {}
}

function goDetail(id) {
  uni.navigateTo({ url: `/pages/product/detail?id=${id}` })
}

function goCart() {
  if (cartCount.value === 0) return
  uni.navigateTo({ url: '/pages/cart/index' })
}

async function init() {
  await ensureLogin()
  await loadCategories()
  await Promise.all([loadProducts(true), loadCart()])
}

onShow(() => init())

onPullDownRefresh(async () => {
  await init()
  uni.stopPullDownRefresh()
})
</script>

<style lang="scss" scoped>
.order-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  padding-bottom: 200rpx;
}

.order-body {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.category-panel {
  width: 180rpx;
  background: #f0f0f0;
  height: 100%;
}

.category-item {
  padding: 28rpx 16rpx;
  text-align: center;
  font-size: 26rpx;
  color: #666;

  &.active {
    background: #fff;
    color: #6c5ce7;
    font-weight: bold;
    border-left: 6rpx solid #6c5ce7;
  }
}

.product-panel {
  flex: 1;
  height: 100%;
  padding: 16rpx;
  box-sizing: border-box;
}

.product-card {
  display: flex;
  background: #fff;
  border-radius: 12rpx;
  padding: 20rpx;
  margin-bottom: 16rpx;
  gap: 20rpx;
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.product-name {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
}

.product-desc {
  font-size: 22rpx;
  color: #999;
  margin: 8rpx 0;
  flex: 1;
}

.product-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.cart-bar {
  position: fixed;
  left: 24rpx;
  right: 24rpx;
  bottom: 120rpx;
  background: #1a1a2e;
  border-radius: 48rpx;
  padding: 16rpx 24rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  z-index: 10;
}

.cart-left {
  display: flex;
  align-items: center;
  gap: 24rpx;
  position: relative;
  padding-left: 16rpx;
}

.cart-total {
  color: #fff;
  font-size: 32rpx;
  font-weight: bold;
}

.empty-wrap {
  padding-top: 120rpx;
}
</style>

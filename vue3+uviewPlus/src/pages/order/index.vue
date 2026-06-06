<template>
  <view class="page">
    <u-navbar title="点餐" :autoBack="false" bgColor="#1a1a2e" leftIcon="" />

    <view class="order-body">
      <!-- 左侧分类 -->
      <scroll-view scroll-y class="category-side">
        <view
          v-for="(cat, idx) in categories"
          :key="cat.id"
          class="cat-item"
          :class="{ active: currentTab === idx }"
          @click="switchCategory(idx, cat)"
        >
          {{ cat.name }}
        </view>
      </scroll-view>

      <!-- 右侧商品 -->
      <scroll-view scroll-y class="product-side" @scrolltolower="loadMore">
        <view v-for="item in products" :key="item.id" class="product-card" @click="goDetail(item.id)">
          <u-image :src="item.image" width="160rpx" height="160rpx" radius="8" />
          <view class="product-info">
            <text class="product-name">{{ item.name }}</text>
            <text class="product-desc">{{ item.description || '精选酒水' }}</text>
            <view class="product-bottom">
              <text class="price">¥{{ formatPrice(item.price) }}</text>
              <u-button
                type="primary"
                size="mini"
                text="加购"
                :disabled="!item.stock"
                @click.stop="addCart(item)"
              />
            </view>
          </view>
        </view>
        <u-empty v-if="!loading && !products.length" mode="list" text="暂无商品" />
        <u-loadmore v-if="products.length" :status="loadStatus" />
      </scroll-view>
    </view>

    <!-- 购物车底栏 -->
    <view class="cart-bar" @click="goCart">
      <view class="cart-icon-wrap">
        <u-icon name="shopping-cart" size="28" color="#fff" />
        <u-badge v-if="cartCount > 0" :value="cartCount" absolute :offset="[-6, -6]" />
      </view>
      <text class="cart-total">¥{{ formatPrice(cartTotal) }}</text>
      <u-button type="primary" text="去结算" size="small" :disabled="cartCount === 0" @click.stop="goCart" />
    </view>

    <AppTabbar active="order" />
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onShow, onPullDownRefresh } from '@dcloudio/uni-app'
import { getCategoryList, getProductList } from '@/api/product'
import { addCart as addCartApi } from '@/api/cart'
import { addToLocalCart, getLocalCart, getCartSummary } from '@/utils/cart'
import { formatPrice } from '@/utils/navigate'

const categories = ref([{ id: 0, name: '全部' }])
const currentTab = ref(0)
const currentCategoryId = ref(null)
const products = ref([])
const page = ref(1)
const loading = ref(false)
const loadStatus = ref('loadmore')
const finished = ref(false)
const localCart = ref([])

const cartCount = computed(() => getCartSummary(localCart.value).count)
const cartTotal = computed(() => getCartSummary(localCart.value).total)

onShow(() => {
  localCart.value = getLocalCart()
  if (!categories.value.length || categories.value.length === 1) {
    loadCategories()
  }
})
onPullDownRefresh(async () => {
  await refreshProducts()
  uni.stopPullDownRefresh()
})

async function loadCategories() {
  try {
    const list = await getCategoryList()
    categories.value = [{ id: 0, name: '全部' }, ...(list || [])]
    refreshProducts()
  } catch (e) {
    uni.showToast({ title: e.msg || '加载分类失败', icon: 'none' })
  }
}

function switchCategory(idx, cat) {
  currentTab.value = idx
  currentCategoryId.value = cat.id === 0 ? null : cat.id
  refreshProducts()
}

async function refreshProducts() {
  page.value = 1
  finished.value = false
  products.value = []
  await fetchProducts()
}

async function fetchProducts() {
  if (finished.value) return
  loading.value = true
  loadStatus.value = 'loading'
  try {
    const params = { page: page.value, pageSize: 20, status: 1 }
    if (currentCategoryId.value) params.categoryId = currentCategoryId.value
    const data = await getProductList(params)
    const items = data?.list || []
    products.value = page.value === 1 ? items : [...products.value, ...items]
    finished.value = items.length < 20
    loadStatus.value = finished.value ? 'nomore' : 'loadmore'
  } catch (e) {
    uni.showToast({ title: e.msg || '加载商品失败', icon: 'none' })
    loadStatus.value = 'nomore'
  } finally {
    loading.value = false
  }
}

function loadMore() {
  if (finished.value || loading.value) return
  page.value++
  fetchProducts()
}

async function addCart(item) {
  try {
    await addCartApi({ productId: item.id, quantity: 1 })
  } catch { /* 购物车接口未实现时使用本地缓存 */ }
  localCart.value = addToLocalCart(item, 1)
  uni.showToast({ title: '已加入购物车', icon: 'success' })
}

function goDetail(id) {
  uni.navigateTo({ url: `/pages/product/detail?id=${id}` })
}

function goCart() {
  if (cartCount.value === 0) return
  uni.navigateTo({ url: '/pages/cart/index' })
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #0f0f1a;
  padding-bottom: 200rpx;
}
.order-body {
  display: flex;
  height: calc(100vh - 88rpx - 180rpx);
}
.category-side {
  width: 180rpx;
  background: #1a1a2e;
}
.cat-item {
  padding: 28rpx 16rpx;
  text-align: center;
  font-size: 26rpx;
  color: #a0a0b8;
  border-left: 4rpx solid transparent;
  &.active {
    color: #7c3aed;
    background: #0f0f1a;
    border-left-color: #7c3aed;
    font-weight: 600;
  }
}
.product-side {
  flex: 1;
  padding: 16rpx;
}
.product-card {
  display: flex;
  gap: 16rpx;
  background: #1a1a2e;
  border-radius: 12rpx;
  padding: 16rpx;
  margin-bottom: 16rpx;
}
.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.product-name {
  font-size: 28rpx;
  font-weight: 600;
  color: #fff;
}
.product-desc {
  font-size: 24rpx;
  color: #6b6b80;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.product-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.price {
  color: #f59e0b;
  font-size: 32rpx;
  font-weight: 600;
}
.cart-bar {
  position: fixed;
  left: 24rpx;
  right: 24rpx;
  bottom: 120rpx;
  height: 96rpx;
  background: #2a2a3e;
  border-radius: 48rpx;
  display: flex;
  align-items: center;
  padding: 0 24rpx;
  gap: 20rpx;
  z-index: 100;
}
.cart-icon-wrap {
  position: relative;
}
.cart-total {
  flex: 1;
  font-size: 32rpx;
  font-weight: 600;
  color: #fff;
}
</style>

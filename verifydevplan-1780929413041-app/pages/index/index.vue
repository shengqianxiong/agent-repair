<template>
  <view class="index-page">
    <view class="nav-bar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="nav-content">
        <text class="nav-title">商品列表</text>
        <view class="logout-btn" @click="handleLogout">
          <u-icon name="arrow-rightward" color="#999999" size="16"></u-icon>
          <text>退出</text>
        </view>
      </view>
    </view>

    <view class="search-wrap" :style="{ top: navBarHeight + 'px' }">
      <u-search
        v-model="keyword"
        placeholder="搜索商品名称"
        shape="round"
        :show-action="false"
        bg-color="#FFFFFF"
        @search="handleSearch"
        @clear="handleSearch"
      ></u-search>
    </view>

    <scroll-view
      class="product-scroll"
      scroll-y
      :style="{ paddingTop: navBarHeight + searchHeight + 'px' }"
      @scrolltolower="loadMore"
    >
      <view v-if="loading && !list.length" class="state-wrap">
        <u-loading-icon mode="circle" color="#7B61FF" size="36"></u-loading-icon>
        <text class="state-text">加载中...</text>
      </view>

      <EmptyState
        v-else-if="!list.length"
        text="暂无商品"
        icon="shopping-cart"
        button-text="刷新"
        @action="refreshList"
      />

      <view v-else class="product-list">
        <view
          v-for="item in list"
          :key="item.id"
          class="product-card"
          @click="showProductDetail(item)"
        >
          <image class="product-image" :src="item.image" mode="aspectFill"></image>
          <view class="product-info">
            <text class="product-name">{{ item.name }}</text>
            <text class="product-desc">{{ item.description }}</text>
            <view class="product-footer">
              <view class="price-wrap">
                <text class="price-current">¥{{ formatPrice(item.price) }}</text>
                <text v-if="item.originalPrice" class="price-original">¥{{ formatPrice(item.originalPrice) }}</text>
              </view>
              <text class="stock-text">库存 {{ item.stock }}</text>
            </view>
          </view>
        </view>
      </view>

      <view v-if="list.length" class="load-more">
        <text v-if="loadingMore">加载更多...</text>
        <text v-else-if="finished">没有更多了</text>
      </view>
    </scroll-view>

    <u-popup :show="detailVisible" mode="bottom" round="24" @close="detailVisible = false">
      <view v-if="currentProduct" class="detail-popup">
        <image class="detail-image" :src="currentProduct.image" mode="aspectFill"></image>
        <text class="detail-name">{{ currentProduct.name }}</text>
        <text class="detail-desc">{{ currentProduct.description }}</text>
        <view class="detail-price-row">
          <text class="detail-price">¥{{ formatPrice(currentProduct.price) }}</text>
          <text class="detail-stock">库存 {{ currentProduct.stock }}</text>
        </view>
        <u-button type="primary" text="关闭" shape="circle" @click="detailVisible = false"></u-button>
      </view>
    </u-popup>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow, onPullDownRefresh } from '@dcloudio/uni-app'
import { getProductList, getProductDetail } from '@/api/products.js'
import { requireAuth, clearAuth } from '@/utils/auth.js'

const statusBarHeight = ref(0)
const navBarHeight = ref(88)
const searchHeight = ref(56)
const keyword = ref('')
const list = ref([])
const page = ref(1)
const pageSize = 20
const total = ref(0)
const loading = ref(false)
const loadingMore = ref(false)
const finished = ref(false)
const detailVisible = ref(false)
const currentProduct = ref(null)

function initLayout() {
  const sys = uni.getSystemInfoSync()
  statusBarHeight.value = sys.statusBarHeight || 0
  navBarHeight.value = statusBarHeight.value + 44
}

function formatPrice(value) {
  return Number(value || 0).toFixed(2)
}

async function fetchList(reset = false) {
  if (reset) {
    page.value = 1
    finished.value = false
    list.value = []
  }
  if (finished.value && !reset) return

  const isFirstPage = page.value === 1
  if (isFirstPage) {
    loading.value = true
  } else {
    loadingMore.value = true
  }

  try {
    const data = await getProductList({
      page: page.value,
      pageSize,
      keyword: keyword.value.trim()
    })
    const newList = data.list || []
    total.value = data.total || 0
    list.value = isFirstPage ? newList : list.value.concat(newList)
    finished.value = list.value.length >= total.value
    if (!finished.value) {
      page.value += 1
    }
  } catch {
    if (isFirstPage) {
      list.value = []
    }
  } finally {
    loading.value = false
    loadingMore.value = false
    uni.stopPullDownRefresh()
  }
}

function refreshList() {
  fetchList(true)
}

function handleSearch() {
  fetchList(true)
}

function loadMore() {
  if (!loading.value && !loadingMore.value && !finished.value) {
    fetchList(false)
  }
}

async function showProductDetail(item) {
  try {
    uni.showLoading({ title: '加载中' })
    currentProduct.value = await getProductDetail(item.id)
    detailVisible.value = true
  } catch {
    // 错误提示由 request 层处理
  } finally {
    uni.hideLoading()
  }
}

function handleLogout() {
  uni.showModal({
    title: '提示',
    content: '确定退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        clearAuth()
        uni.reLaunch({ url: '/pages/login/login' })
      }
    }
  })
}

onShow(() => {
  if (!requireAuth()) return
  initLayout()
  refreshList()
})

onPullDownRefresh(() => {
  refreshList()
})
</script>

<style lang="scss" scoped>
.index-page {
  min-height: 100vh;
  background: var(--color-background);
}

.nav-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  background: var(--color-card-bg);
  box-shadow: var(--shadow-card);
}

.nav-content {
  height: 88rpx;
  padding: 0 var(--page-padding);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.nav-title {
  font-size: var(--font-size-lg);
  font-weight: 600;
  color: var(--color-text-main);
}

.logout-btn {
  display: flex;
  align-items: center;
  gap: 8rpx;
  font-size: var(--font-size-xs);
  color: var(--color-text-tertiary);
}

.search-wrap {
  position: fixed;
  left: 0;
  right: 0;
  z-index: 99;
  padding: var(--spacing-sm) var(--page-padding);
  background: var(--color-background);
}

.product-scroll {
  height: 100vh;
  box-sizing: border-box;
}

.state-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx 0;
  gap: var(--spacing-sm);
}

.state-text {
  font-size: var(--font-size-sm);
  color: var(--color-text-tertiary);
}

.product-list {
  padding: var(--spacing-sm) var(--page-padding) var(--spacing-xl);
  display: flex;
  flex-direction: column;
  gap: var(--card-gap);
}

.product-card {
  display: flex;
  background: var(--color-card-bg);
  border-radius: var(--radius-md);
  overflow: hidden;
  box-shadow: var(--shadow-card);
}

.product-image {
  width: 200rpx;
  height: 200rpx;
  flex-shrink: 0;
}

.product-info {
  flex: 1;
  padding: var(--spacing-md);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-width: 0;
}

.product-name {
  font-size: var(--font-size-base);
  font-weight: 600;
  color: var(--color-text-main);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-desc {
  font-size: var(--font-size-xs);
  color: var(--color-text-tertiary);
  margin-top: var(--spacing-xs);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-footer {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-top: var(--spacing-sm);
}

.price-wrap {
  display: flex;
  align-items: baseline;
  gap: var(--spacing-xs);
}

.price-current {
  font-size: var(--font-size-lg);
  font-weight: 600;
  color: var(--color-primary);
}

.price-original {
  font-size: var(--font-size-xs);
  color: var(--color-text-tertiary);
  text-decoration: line-through;
}

.stock-text {
  font-size: var(--font-size-xs);
  color: var(--color-text-tertiary);
}

.load-more {
  text-align: center;
  padding: var(--spacing-md) 0 var(--spacing-xl);
  font-size: var(--font-size-xs);
  color: var(--color-text-tertiary);
}

.detail-popup {
  padding: var(--spacing-base);
  padding-bottom: calc(var(--spacing-base) + env(safe-area-inset-bottom));
}

.detail-image {
  width: 100%;
  height: 360rpx;
  border-radius: var(--radius-md);
  margin-bottom: var(--spacing-md);
}

.detail-name {
  display: block;
  font-size: var(--font-size-lg);
  font-weight: 600;
  color: var(--color-text-main);
  margin-bottom: var(--spacing-xs);
}

.detail-desc {
  display: block;
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  margin-bottom: var(--spacing-md);
}

.detail-price-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-md);
}

.detail-price {
  font-size: var(--font-size-xl);
  font-weight: 600;
  color: var(--color-primary);
}

.detail-stock {
  font-size: var(--font-size-sm);
  color: var(--color-text-tertiary);
}
</style>

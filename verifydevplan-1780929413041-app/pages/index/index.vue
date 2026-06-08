<template>
  <view class="page">
    <view class="search-bar">
      <u-search
        v-model="keyword"
        placeholder="搜索商品名称"
        shape="round"
        :show-action="false"
        bg-color="#F7F7F7"
        @search="handleSearch"
        @clear="handleSearch"
      ></u-search>
    </view>

    <view v-if="loading && !list.length" class="loading-wrap">
      <u-loading-icon mode="circle" color="#7B61FF" size="36"></u-loading-icon>
      <text class="loading-text">加载中...</text>
    </view>

    <view v-else-if="!list.length" class="empty-wrap">
      <EmptyState
        text="暂无商品"
        icon="shopping-cart"
        button-text="刷新"
        @action="loadProducts"
      ></EmptyState>
    </view>

    <scroll-view v-else scroll-y class="list-scroll" @scrolltolower="loadMore">
      <view
        v-for="item in list"
        :key="item.id"
        class="product-card"
        @click="showProduct(item)"
      >
        <image class="product-image" :src="item.image" mode="aspectFill"></image>
        <view class="product-info">
          <text class="product-name">{{ item.name }}</text>
          <text class="product-desc">{{ item.desc }}</text>
          <view class="product-footer">
            <text class="product-price">¥{{ formatMoney(item.price) }}</text>
            <text class="product-stock">库存 {{ item.stock }}</text>
          </view>
        </view>
      </view>
      <view v-if="loadingMore" class="load-more">
        <u-loading-icon mode="circle" color="#7B61FF" size="24"></u-loading-icon>
        <text>加载更多...</text>
      </view>
      <view v-else-if="finished" class="load-more finished">已加载全部</view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onPullDownRefresh, onShow } from '@dcloudio/uni-app'
import { getProductList } from '@/api/products.js'
import { formatMoney, showToast } from '@/utils/common.js'
import { isLoggedIn } from '@/utils/auth.js'
import EmptyState from '@/components/empty-state/empty-state.vue'

const keyword = ref('')
const list = ref([])
const loading = ref(false)
const loadingMore = ref(false)
const finished = ref(false)
const page = ref(1)
const pageSize = 20
const total = ref(0)

async function loadProducts(reset = true) {
  if (reset) {
    page.value = 1
    finished.value = false
    loading.value = true
  } else {
    if (finished.value || loadingMore.value) return
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
    list.value = reset ? newList : [...list.value, ...newList]
    finished.value = list.value.length >= total.value
  } catch (err) {
    console.error('load products failed', err)
  } finally {
    loading.value = false
    loadingMore.value = false
    uni.stopPullDownRefresh()
  }
}

function handleSearch() {
  loadProducts(true)
}

function loadMore() {
  if (finished.value) return
  page.value += 1
  loadProducts(false)
}

function showProduct(item) {
  showToast(`${item.name} ¥${formatMoney(item.price)}`)
}

onShow(() => {
  if (!isLoggedIn()) {
    uni.reLaunch({ url: '/pages/login/login' })
    return
  }
  loadProducts(true)
})

onPullDownRefresh(() => {
  loadProducts(true)
})
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--color-background);
}

.search-bar {
  padding: var(--spacing-md) var(--page-padding);
  background: var(--color-card-bg);
  border-bottom: 1rpx solid var(--color-border);
}

.loading-wrap,
.empty-wrap {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.loading-text {
  margin-top: var(--spacing-md);
  font-size: var(--font-size-sm);
  color: var(--color-text-tertiary);
}

.list-scroll {
  flex: 1;
  height: calc(100vh - 120rpx);
  padding: var(--spacing-md) var(--page-padding);
  box-sizing: border-box;
}

.product-card {
  display: flex;
  background: var(--color-card-bg);
  border-radius: var(--radius-md);
  padding: var(--spacing-md);
  margin-bottom: var(--spacing-md);
  box-shadow: var(--shadow-card);
}

.product-image {
  width: 180rpx;
  height: 180rpx;
  border-radius: var(--radius-sm);
  flex-shrink: 0;
  background: var(--color-input-bg);
}

.product-info {
  flex: 1;
  margin-left: var(--spacing-md);
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
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  margin-top: var(--spacing-sm);
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: var(--spacing-md);
}

.product-price {
  font-size: var(--font-size-lg);
  font-weight: 600;
  color: var(--color-primary);
}

.product-stock {
  font-size: 24rpx;
  color: var(--color-text-tertiary);
}

.load-more {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-lg) 0;
  font-size: 24rpx;
  color: var(--color-text-tertiary);
}

.load-more.finished {
  gap: 0;
}
</style>

<template>
  <view class="page">
    <LoadingOverlay :show="loading" />
    <view class="navbar">
      <view class="nav-left"><u-icon name="list" color="#7B61FF" size="22"></u-icon></view>
      <text class="nav-title">广场动态</text>
      <view class="nav-right">
        <u-icon name="bell" color="#7B61FF" size="20"></u-icon>
        <view class="red-dot"></view>
      </view>
    </view>

    <view class="filter-row">
      <view
        v-for="f in quickFilters"
        :key="f.key"
        class="filter-btn"
        :class="{ active: activeFilter === f.key }"
        @click="activeFilter = f.key"
      >{{ f.label }}</view>
    </view>

    <view class="category-tabs">
      <view
        v-for="tab in tabs"
        :key="tab.key"
        class="tab-item"
        :class="{ active: activeTab === tab.key }"
        @click="activeTab = tab.key"
      >{{ tab.label }}</view>
    </view>

    <scroll-view scroll-y class="feed-area" @scrolltolower="loadMore">
      <view v-if="feedList.length === 0 && !loading" class="empty-wrap"><EmptyState text="暂无动态" /></view>
      <view class="masonry">
        <view class="masonry-col">
          <view v-for="item in leftCol" :key="item.id" class="feed-card" @click="goProfile(item.userId)">
            <image class="feed-img" :src="item.images[0]" mode="widthFix" :style="{ height: item.height + 'rpx' }" />
            <view v-if="item.title" class="feed-overlay">{{ item.title }}</view>
            <view class="feed-footer">
              <image class="feed-avatar" :src="item.avatar" />
              <text class="feed-name">{{ item.nickname }}</text>
              <view class="feed-like"><u-icon name="thumb-up" size="12" color="#999"></u-icon><text>{{ item.likes }}</text></view>
            </view>
            <view class="feed-tags">
              <text class="tag">{{ item.age }}岁</text>
              <text class="tag">{{ item.distance }}</text>
            </view>
          </view>
        </view>
        <view class="masonry-col">
          <view v-for="item in rightCol" :key="item.id" class="feed-card" @click="goProfile(item.userId)">
            <image class="feed-img" :src="item.images[0]" mode="widthFix" :style="{ height: item.height + 'rpx' }" />
            <view v-if="item.title" class="feed-overlay">{{ item.title }}</view>
            <view class="feed-footer">
              <image class="feed-avatar" :src="item.avatar" />
              <text class="feed-name">{{ item.nickname }}</text>
              <view class="feed-like"><u-icon name="thumb-up" size="12" color="#999"></u-icon><text>{{ item.likes }}</text></view>
            </view>
            <view class="feed-tags">
              <text class="tag">{{ item.age }}岁</text>
              <text class="tag">{{ item.distance }}</text>
            </view>
          </view>
        </view>
      </view>
    </scroll-view>

    <view class="fab" @click="showToast('发布动态')">
      <u-icon name="plus" color="#FFFFFF" size="24"></u-icon>
    </view>
    <AppTabbar current="square" />
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getSquareFeed } from '@/api/index.js'
import { showToast } from '@/utils/common.js'

const loading = ref(false)
const feedList = ref([])
const activeFilter = ref('nearby')
const activeTab = ref('recommend')

const quickFilters = [
  { key: 'nearby', label: '附近搭子' },
  { key: 'hot', label: '今日热门' },
  { key: 'meal', label: '约个饭' }
]
const tabs = [
  { key: 'recommend', label: '推荐' },
  { key: 'nearby', label: '附近' },
  { key: 'latest', label: '最新' }
]

const leftCol = computed(() => feedList.value.filter((_, i) => i % 2 === 0))
const rightCol = computed(() => feedList.value.filter((_, i) => i % 2 === 1))

async function loadData() {
  loading.value = true
  try {
    const res = await getSquareFeed({ filter: activeFilter.value, tab: activeTab.value })
    feedList.value = res?.list || []
  } finally { loading.value = false }
}

function goProfile(userId) { uni.navigateTo({ url: `/pages/social/profile?id=${userId}` }) }
function loadMore() { /* 加载更多 */ }

onMounted(loadData)
</script>

<style lang="scss" scoped>
.page { min-height: 100vh; background: var(--color-background); }
.navbar {
  display: flex; align-items: center; height: 88rpx; padding: 0 32rpx; background: #FFF;
  .nav-title { flex: 1; text-align: center; font-size: 36rpx; font-weight: 700; color: #7B61FF; }
  .nav-left, .nav-right { width: 60rpx; position: relative; }
  .red-dot { position: absolute; top: 0; right: 0; width: 12rpx; height: 12rpx; border-radius: 50%; background: #FF4D4F; }
}
.filter-row { display: flex; gap: 16rpx; padding: 20rpx 32rpx; }
.filter-btn {
  padding: 12rpx 28rpx; border-radius: 200rpx; font-size: 26rpx; background: #F5F5F5; color: #666;
  &.active { background: #7B61FF; color: #FFF; }
}
.category-tabs {
  display: flex; padding: 0 32rpx 20rpx; gap: 40rpx; border-bottom: 1rpx solid #F0F0F0;
  .tab-item { font-size: 30rpx; color: #666; padding-bottom: 12rpx;
    &.active { color: #7B61FF; font-weight: 600; border-bottom: 4rpx solid #7B61FF; }
  }
}
.feed-area { height: calc(100vh - 340rpx); padding: 20rpx 32rpx; }
.masonry { display: flex; gap: 20rpx; }
.masonry-col { flex: 1; }
.feed-card {
  background: #FFF; border-radius: 40rpx; overflow: hidden; margin-bottom: 20rpx; box-shadow: var(--shadow-card); position: relative;
  .feed-img { width: 100%; display: block; border-radius: 40rpx 40rpx 0 0; }
  .feed-overlay { position: absolute; bottom: 120rpx; left: 0; right: 0; padding: 16rpx; background: linear-gradient(transparent, rgba(0,0,0,0.5)); color: #FFF; font-size: 26rpx; }
  .feed-footer { display: flex; align-items: center; padding: 16rpx; gap: 8rpx; }
  .feed-avatar { width: 40rpx; height: 40rpx; border-radius: 50%; }
  .feed-name { flex: 1; font-size: 24rpx; color: #333; }
  .feed-like { display: flex; align-items: center; gap: 4rpx; font-size: 22rpx; color: #999; }
  .feed-tags { display: flex; gap: 8rpx; padding: 0 16rpx 16rpx; }
  .tag { background: #EBE4FF; color: #7B61FF; font-size: 20rpx; padding: 4rpx 12rpx; border-radius: 200rpx; }
}
.fab {
  position: fixed; right: 32rpx; bottom: 160rpx; width: 112rpx; height: 112rpx; border-radius: 50%;
  background: linear-gradient(135deg, #7246F2, #9D7BFF); display: flex; align-items: center; justify-content: center;
  box-shadow: var(--shadow-fab); z-index: 100;
}
</style>

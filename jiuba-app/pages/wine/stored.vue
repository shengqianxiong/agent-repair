<template>
  <view class="page">
    <LoadingOverlay :show="loading" />
    <view class="navbar">
      <view class="nav-left"><u-icon name="list" color="#7B61FF" size="22"></u-icon></view>
      <text class="nav-title">云享生活</text>
      <view class="nav-right"><u-icon name="bell" color="#7B61FF" size="20"></u-icon></view>
    </view>

    <view class="search-bar">
      <u-icon name="search" color="#999" size="18"></u-icon>
      <input v-model="keyword" class="search-input" placeholder="搜索我的存酒..." @confirm="loadData" />
    </view>

    <view class="list-header">
      <text class="list-title">我的存酒 ({{ wineList.length }})</text>
      <u-icon name="hourglass" color="#999" size="18" @click="showToast('筛选')"></u-icon>
    </view>

    <scroll-view scroll-y class="wine-list">
      <view v-if="wineList.length === 0 && !loading" class="empty-wrap"><EmptyState text="暂无存酒记录" /></view>
      <view v-for="wine in wineList" :key="wine.id" class="wine-card">
        <view class="wine-img-wrap">
          <image class="wine-img" :src="wine.image" mode="aspectFill" />
          <view class="remain-tag">剩余 {{ wine.remaining }} 瓶</view>
        </view>
        <view class="wine-info">
          <text class="wine-name">{{ wine.name }}</text>
          <text class="wine-cat">{{ wine.category }}</text>
          <text class="wine-date">存入日期：{{ wine.storeDate }}</text>
          <view class="wine-actions">
            <view class="btn-primary" @click="retrieve(wine)">取酒</view>
            <view class="btn-secondary" @click="renew(wine)">续存</view>
          </view>
        </view>
      </view>
    </scroll-view>
    <AppTabbar current="my" />
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getStoredWineList, retrieveWine, renewWine } from '@/api/index.js'
import { showToast, showLoading, hideLoading } from '@/utils/common.js'

const loading = ref(false)
const keyword = ref('')
const wineList = ref([])

async function loadData() {
  loading.value = true
  try {
    const res = await getStoredWineList({ keyword: keyword.value })
    wineList.value = res?.list || []
  } finally { loading.value = false }
}

async function retrieve(wine) {
  showLoading()
  try { await retrieveWine({ id: wine.id }); showToast('取酒申请已提交'); loadData() } finally { hideLoading() }
}

async function renew(wine) {
  showLoading()
  try { await renewWine({ id: wine.id }); showToast('续存成功'); loadData() } finally { hideLoading() }
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.page { min-height: 100vh; background: #F8F9FB; }
.navbar {
  display: flex; align-items: center; height: 88rpx; padding: 0 32rpx; background: #FFF;
  .nav-title { flex: 1; text-align: center; font-size: 36rpx; font-weight: 700; color: #7B61FF; }
  .nav-left, .nav-right { width: 60rpx; }
}
.search-bar {
  display: flex; align-items: center; margin: 20rpx 32rpx; background: #F5F5F5; border-radius: 40rpx; padding: 16rpx 24rpx; gap: 12rpx;
  .search-input { flex: 1; font-size: 28rpx; }
}
.list-header {
  display: flex; justify-content: space-between; align-items: center; padding: 0 32rpx 20rpx;
  .list-title { font-size: 32rpx; font-weight: 700; color: #1A1A1A; }
}
.wine-list { height: calc(100vh - 340rpx); padding: 0 32rpx; }
.wine-card {
  background: #FFF; border-radius: 30rpx; overflow: hidden; margin-bottom: 24rpx; box-shadow: var(--shadow-card);
  .wine-img-wrap { position: relative; height: 300rpx; }
  .wine-img { width: 100%; height: 100%; }
  .remain-tag { position: absolute; top: 20rpx; right: 20rpx; background: #7B61FF; color: #FFF; font-size: 24rpx; padding: 8rpx 20rpx; border-radius: 200rpx; }
  .wine-info { padding: 24rpx 32rpx 32rpx; }
  .wine-name { font-size: 32rpx; font-weight: 700; color: #1A1A1A; display: block; }
  .wine-cat { font-size: 26rpx; color: #7B61FF; margin-top: 8rpx; display: block; }
  .wine-date { font-size: 24rpx; color: #999; margin-top: 8rpx; display: block; }
  .wine-actions { display: flex; gap: 16rpx; margin-top: 24rpx; }
  .btn-primary { flex: 1; background: #7B61FF; color: #FFF; text-align: center; padding: 20rpx; border-radius: 200rpx; font-size: 28rpx; font-weight: 600; }
  .btn-secondary { flex: 1; background: #EBE4FF; color: #7B61FF; text-align: center; padding: 20rpx; border-radius: 200rpx; font-size: 28rpx; }
}
</style>

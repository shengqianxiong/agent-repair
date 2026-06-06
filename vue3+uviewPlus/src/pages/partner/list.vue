<template>
  <view class="page">
    <view v-if="list.length" class="partner-list">
      <view v-for="item in list" :key="item.id" class="partner-card">
        <u-avatar :src="item.avatar" size="56" />
        <view class="info">
          <view class="top-row">
            <text class="nickname">{{ item.nickname }}</text>
            <u-tag :text="item.onlineStatus || '离线'" size="mini" :type="item.onlineStatus === '在线' ? 'success' : 'info'" />
          </view>
          <view class="tags">
            <u-tag v-for="tag in (item.tags || []).slice(0, 3)" :key="tag" :text="tag" size="mini" plain />
          </view>
          <text class="distance">{{ item.distance }}km</text>
        </view>
        <view class="actions">
          <u-button size="mini" type="primary" text="聊天" @click="goChat(item)" />
          <u-button size="mini" text="详情" @click="goDetail(item.id)" />
        </view>
      </view>
      <u-loadmore :status="loadStatus" />
    </view>
    <u-empty v-else mode="list" text="附近暂无酒友" />
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow, onPullDownRefresh } from '@dcloudio/uni-app'
import { getNearbyPartners } from '@/api/partner'

const list = ref([])
const loadStatus = ref('loadmore')

onShow(() => loadPartners())
onPullDownRefresh(async () => {
  await loadPartners()
  uni.stopPullDownRefresh()
})

async function loadPartners() {
  loadStatus.value = 'loading'
  try {
    const location = await getLocation()
    const data = await getNearbyPartners({
      latitude: location.latitude,
      longitude: location.longitude
    })
    list.value = data?.list || data || []
  } catch {
    list.value = []
  } finally {
    loadStatus.value = 'nomore'
  }
}

function getLocation() {
  return new Promise((resolve, reject) => {
    uni.getLocation({
      type: 'gcj02',
      success: resolve,
      fail: () => {
        uni.showModal({
          title: '需要定位权限',
          content: '请授权地理位置以发现附近酒友',
          success: (res) => {
            if (res.confirm) {
              uni.openSetting({})
            }
          }
        })
        reject(new Error('no location'))
      }
    })
  })
}

function goDetail(id) {
  uni.navigateTo({ url: `/pages/partner/detail?id=${id}` })
}

function goChat(item) {
  uni.navigateTo({ url: `/pages/chat/index?partnerId=${item.id}&name=${item.nickname}` })
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #0f0f1a;
  padding: 16rpx;
}
.partner-card {
  display: flex;
  gap: 16rpx;
  background: #1a1a2e;
  border-radius: 12rpx;
  padding: 20rpx;
  margin-bottom: 16rpx;
  align-items: center;
}
.info { flex: 1; }
.top-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
}
.nickname {
  font-size: 30rpx;
  font-weight: 600;
  color: #fff;
}
.tags {
  display: flex;
  gap: 8rpx;
  margin: 8rpx 0;
  flex-wrap: wrap;
}
.distance {
  font-size: 24rpx;
  color: #6b6b80;
}
.actions {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}
</style>

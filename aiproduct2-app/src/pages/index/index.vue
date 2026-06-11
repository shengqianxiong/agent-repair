<template>
  <view class="home-page">
    <view class="nav-bar">
      <view class="brand">
        <view class="leaf-icon">🍀</view>
        <text class="brand-title">旧衣回收</text>
      </view>
      <view class="nav-actions">
        <u-icon name="chat" size="36rpx" color="#6B7280" />
        <u-icon name="bell" size="36rpx" color="#6B7280" />
      </view>
    </view>

    <scroll-view scroll-y class="content-scroll">
      <view class="hero card-box">
        <view>
          <view class="hero-title">旧衣新生</view>
          <view class="hero-title">绿色未来</view>
          <view class="hero-sub">让闲置衣物温暖更多人</view>
        </view>
        <view class="hero-earth">🌍</view>
      </view>

      <view class="entry-grid">
        <view class="entry-card reserve" @click="goReserve">
          <view class="entry-title">预约免费上门</view>
          <view class="entry-desc">5kg起免费上门收</view>
          <u-icon name="arrow-right" size="24rpx" color="#FFFFFF" />
        </view>
        <view class="entry-card certificate" @click="goCertificate">
          <view class="entry-title">捐赠获取证书</view>
          <view class="entry-desc">助力公益项目</view>
          <u-icon name="arrow-right" size="24rpx" color="#FFFFFF" />
        </view>
      </view>

      <view class="small-card card-box">
        <view class="small-left">
          <text class="icon-box">♻️</text>
          <view>
            <view class="small-title">回收规则</view>
            <view class="small-sub">5kg起且衣物完整免费上门回收</view>
          </view>
        </view>
        <view class="small-link">查看详情</view>
      </view>

      <view class="small-card card-box">
        <view class="small-left">
          <text class="icon-box">🎁</text>
          <view>
            <view class="small-title">奖励政策</view>
            <view class="small-sub">每1kg奖励0.5元环保金</view>
          </view>
        </view>
        <view class="small-link" @click="goReward">查看详情</view>
      </view>

      <view class="section-header">
        <text class="section-title">公益项目</text>
        <text class="section-more">更多 ></text>
      </view>

      <view class="project-grid">
        <view v-for="item in homeProjects" :key="item.id" class="project-card card-box">
          <view class="project-image">{{ item.emoji }}</view>
          <view class="project-name">{{ item.name }}</view>
          <view class="project-weight">{{ item.weight }}kg</view>
          <u-line-progress :percentage="item.progress" activeColor="#2EAE5E" height="10" />
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { onPullDownRefresh } from '@dcloudio/uni-app'
import { onMounted, ref } from 'vue'
import { fetchHomeData } from '@/api'

const homeProjects = ref([
  { id: 1, name: '爱心衣物温暖行动', weight: 1200, progress: 72, emoji: '👨‍👩‍👧‍👦' },
  { id: 2, name: '山区儿童关爱计划', weight: 980, progress: 65, emoji: '❤️' }
])

async function loadData() {
  try {
    const data = await fetchHomeData()
    if (Array.isArray(data?.projectList) && data.projectList.length) {
      homeProjects.value = data.projectList
    }
  } catch (error) {
    // 接口未接通时使用页面内置模拟数据，保证演示完整性
  } finally {
    uni.stopPullDownRefresh()
  }
}

function goReserve() {
  uni.navigateTo({ url: '/pages/reserve/index' })
}

function goReward() {
  uni.switchTab({ url: '/pages/reward/index' })
}

function goCertificate() {
  uni.navigateTo({ url: '/pages/certificate/index' })
}

onPullDownRefresh(() => {
  loadData()
})

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.home-page {
  min-height: 100vh;
  background: var(--color-page-bg);
  padding: calc(88rpx + env(safe-area-inset-top)) 24rpx 24rpx;
  box-sizing: border-box;
}

.nav-bar {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  padding: calc(16rpx + env(safe-area-inset-top)) 24rpx 16rpx;
  background: #ffffff;
  z-index: 9;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-sizing: border-box;
}

.brand {
  display: flex;
  align-items: center;
  gap: 10rpx;
}

.leaf-icon {
  font-size: 34rpx;
}

.brand-title {
  font-size: 34rpx;
  font-weight: 700;
  color: #3b3f45;
}

.nav-actions {
  display: flex;
  gap: 24rpx;
}

.content-scroll {
  height: calc(100vh - 120rpx - env(safe-area-inset-top));
}

.hero {
  margin-top: 8rpx;
  padding: 30rpx;
  background: linear-gradient(135deg, #cbf3bf 0%, #86d57a 46%, #5abb54 100%);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.hero-title {
  font-size: 52rpx;
  line-height: 1.1;
  font-weight: 700;
  color: #2f8f33;
}

.hero-sub {
  margin-top: 14rpx;
  color: #3a7d3b;
  font-size: 24rpx;
}

.hero-earth {
  font-size: 96rpx;
}

.entry-grid {
  margin-top: 20rpx;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14rpx;
}

.entry-card {
  border-radius: 16rpx;
  padding: 22rpx 24rpx;
  color: #ffffff;
}

.entry-card.reserve {
  background: linear-gradient(120deg, #2ca44f 0%, #1e8b44 100%);
}

.entry-card.certificate {
  background: linear-gradient(120deg, #368fee 0%, #2178d7 100%);
}

.entry-title {
  font-size: 30rpx;
  font-weight: 600;
}

.entry-desc {
  margin: 8rpx 0 6rpx;
  font-size: 22rpx;
  opacity: 0.95;
}

.small-card {
  margin-top: 16rpx;
  padding: 24rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.small-left {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.icon-box {
  width: 54rpx;
  height: 54rpx;
  border-radius: 14rpx;
  text-align: center;
  line-height: 54rpx;
  font-size: 34rpx;
  background: #f7fbf5;
}

.small-title {
  color: #2f3338;
  font-size: 28rpx;
  font-weight: 600;
}

.small-sub {
  margin-top: 8rpx;
  color: var(--color-text-secondary);
  font-size: 22rpx;
}

.small-link {
  color: #1f9c50;
  font-size: 24rpx;
}

.section-header {
  margin: 24rpx 4rpx 14rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-more {
  color: var(--color-text-light);
  font-size: 24rpx;
}

.project-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16rpx;
  padding-bottom: calc(32rpx + env(safe-area-inset-bottom));
}

.project-card {
  padding: 16rpx;
}

.project-image {
  height: 158rpx;
  border-radius: 14rpx;
  margin-bottom: 10rpx;
  background: #f4f8ef;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 70rpx;
}

.project-name {
  font-size: 24rpx;
  font-weight: 500;
  color: #2f3338;
}

.project-weight {
  margin: 8rpx 0 12rpx;
  font-size: 22rpx;
  color: #7e838b;
}
</style>

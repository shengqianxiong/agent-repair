<template>
  <view class="page">
    <LoadingOverlay :show="loading" />
    <AppNavbar title="个人主页" right-icon="more-dot-fill" bg-color="transparent" />

    <scroll-view scroll-y class="content">
      <view class="profile-card">
        <view class="card-top">
          <image class="avatar" :src="profile.avatar" mode="aspectFill" />
          <view class="dm-btn" @click="goChat">私信TA</view>
        </view>
        <text class="nickname">{{ profile.nickname }}</text>
        <view class="tags">
          <view class="gender-tag">
            <u-icon :name="profile.gender === 2 ? 'woman' : 'man'" :color="profile.gender === 2 ? '#FF69B4' : '#7B61FF'" size="14"></u-icon>
            <text>{{ profile.age }}岁</text>
          </view>
          <view class="location-tag">
            <u-icon name="map" color="#999" size="12"></u-icon>
            <text>{{ profile.location }}</text>
          </view>
        </view>
        <text class="bio">{{ profile.bio }}</text>
      </view>

      <view class="section-header">
        <text class="section-title">TA的动态</text>
        <text class="section-count">共{{ profile.postCount }}篇</text>
      </view>

      <view class="masonry">
        <view class="masonry-col">
          <view v-for="post in leftPosts" :key="post.id" class="post-card" @click="showToast('动态详情')">
            <image class="post-img" :src="post.image" mode="widthFix" :style="{ height: post.height + 'rpx' }" />
            <view v-if="post.title" class="post-overlay">{{ post.title }}</view>
          </view>
        </view>
        <view class="masonry-col">
          <view v-for="post in rightPosts" :key="post.id" class="post-card" @click="showToast('动态详情')">
            <image class="post-img" :src="post.image" mode="widthFix" :style="{ height: post.height + 'rpx' }" />
            <view v-if="post.title" class="post-overlay">{{ post.title }}</view>
          </view>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getSocialProfile } from '@/api/index.js'
import { showToast } from '@/utils/common.js'

const loading = ref(false)
const userId = ref('')
const profile = ref({ posts: [] })

const leftPosts = computed(() => profile.value.posts?.filter((_, i) => i % 2 === 0) || [])
const rightPosts = computed(() => profile.value.posts?.filter((_, i) => i % 2 === 1) || [])

async function loadProfile() {
  loading.value = true
  try { profile.value = await getSocialProfile(userId.value) || {} } finally { loading.value = false }
}

function goChat() { showToast('进入聊天') }

onLoad((q) => { userId.value = q.id || '2001'; loadProfile() })
</script>

<style lang="scss" scoped>
.page { min-height: 100vh; background: #FFF5F5; }
.content { height: calc(100vh - 88rpx); padding: 0 32rpx 32rpx; }
.profile-card {
  background: #FFF; border-radius: 48rpx; padding: 40rpx; margin-bottom: 32rpx; box-shadow: var(--shadow-card);
  .card-top { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 20rpx; }
  .avatar { width: 140rpx; height: 140rpx; border-radius: 50%; }
  .dm-btn { background: #7B61FF; color: #FFF; padding: 12rpx 28rpx; border-radius: 200rpx; font-size: 26rpx; }
  .nickname { font-size: 40rpx; font-weight: 700; color: #1A1A1A; display: block; }
  .tags { display: flex; gap: 16rpx; margin: 16rpx 0; }
  .gender-tag { display: flex; align-items: center; gap: 6rpx; background: #FFEDED; padding: 6rpx 16rpx; border-radius: 200rpx; font-size: 24rpx; color: #FF69B4; }
  .location-tag { display: flex; align-items: center; gap: 6rpx; font-size: 24rpx; color: #999; }
  .bio { font-size: 28rpx; color: #666; line-height: 1.6; }
}
.section-header { display: flex; justify-content: space-between; margin-bottom: 20rpx; }
.section-title { font-size: 32rpx; font-weight: 700; }
.section-count { font-size: 26rpx; color: #999; }
.masonry { display: flex; gap: 20rpx; }
.masonry-col { flex: 1; }
.post-card { border-radius: 32rpx; overflow: hidden; margin-bottom: 20rpx; position: relative; }
.post-img { width: 100%; display: block; border-radius: 32rpx; }
.post-overlay { position: absolute; bottom: 0; left: 0; right: 0; padding: 16rpx; background: linear-gradient(transparent, rgba(0,0,0,0.5)); color: #FFF; font-size: 26rpx; border-radius: 0 0 32rpx 32rpx; }
</style>

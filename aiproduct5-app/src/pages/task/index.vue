<template>
  <view class="dp-page task-page dp-safe-bottom">
    <view class="hero">
      <u-navbar
        title="豆评助手"
        :autoBack="false"
        bgColor="transparent"
        titleStyle="color:#fff;font-weight:700"
        leftIconColor="#fff"
      >
        <template #left>
          <view class="brand-dot">
            <u-icon name="chat-fill" color="#1463ff" size="20"></u-icon>
          </view>
        </template>
        <template #right>
          <u-icon name="grid" color="#fff" size="20" @click="goCenter"></u-icon>
        </template>
      </u-navbar>

      <view class="hero-card">
        <view>
          <text class="hero-label">邀请您体验并评价</text>
          <text class="hero-title">{{ activity.productName }}</text>
          <text class="hero-desc">{{ activity.shopName }} · 返利 ¥{{ activity.rebateAmount }}</text>
        </view>
        <image class="hero-image" :src="activity.productImage" mode="aspectFill"></image>
      </view>
    </view>

    <scroll-view scroll-y class="content" refresher-enabled :refresher-triggered="refreshing" @refresherrefresh="refreshData">
      <view class="progress-card">
        <view class="progress-step active">
          <text class="step-index">1</text>
          <text>复制文案</text>
        </view>
        <view class="progress-line"></view>
        <view class="progress-step active">
          <text class="step-index">2</text>
          <text>去抖音评价</text>
        </view>
        <view class="progress-line"></view>
        <view class="progress-step">
          <text class="step-index">3</text>
          <text>提交截图</text>
        </view>
      </view>

      <view class="section-card product-card">
        <view class="section-header">
          <view>
            <text class="section-title">任务商品</text>
            <text class="section-subtitle">有效期至 {{ activity.validEndTime }}</text>
          </view>
          <u-tag :text="`¥${activity.rebateAmount}`" type="warning" plain size="mini"></u-tag>
        </view>
        <view class="product-row">
          <image class="product-image" :src="activity.productImage" mode="aspectFill"></image>
          <view class="product-info">
            <text class="product-name">{{ activity.productName }}</text>
            <text class="product-desc">{{ activity.title }}</text>
            <view class="product-meta">
              <u-icon name="clock" color="#98a2b3" size="14"></u-icon>
              <text>完成评价后提交截图，商家审核通过即返利</text>
            </view>
          </view>
        </view>
      </view>

      <view class="section-card">
        <view class="section-header">
          <view>
            <text class="section-title">AI 生成文案</text>
            <text class="section-subtitle">选择一条复制，发布时可微调语气</text>
          </view>
          <text class="count-text">{{ selectedCopyIndex + 1 }}/{{ copywritingList.length }}</text>
        </view>
        <view class="copy-tabs">
          <view
            v-for="(item, index) in copywritingList"
            :key="item.id"
            class="copy-tab"
            :class="{ active: selectedCopyIndex === index }"
            @click="selectedCopyIndex = index"
          >
            {{ item.title }}
          </view>
        </view>
        <view class="copy-box">
          <text>{{ selectedCopy.content }}</text>
        </view>
        <u-button type="primary" text="复制评价文案" shape="circle" :customStyle="primaryButtonStyle" @click="copyText"></u-button>
      </view>

      <view class="section-card">
        <view class="section-header">
          <view>
            <text class="section-title">推荐图片</text>
            <text class="section-subtitle">可保存后搭配评价发布</text>
          </view>
        </view>
        <scroll-view scroll-x class="image-scroll" show-scrollbar="false">
          <view class="image-list">
            <view v-for="(image, index) in activity.recommendImages" :key="image" class="image-item" @click="previewImage(index)">
              <image class="recommend-image" :src="image" mode="aspectFill"></image>
              <view class="save-chip" @click.stop="saveImage(image)">
                <u-icon name="download" color="#fff" size="13"></u-icon>
                <text>保存</text>
              </view>
            </view>
          </view>
        </scroll-view>
      </view>

      <view class="tips-card">
        <u-icon name="checkmark-circle-fill" color="#25c06d" size="20"></u-icon>
        <view>
          <text class="tips-title">发布后记得截图</text>
          <text class="tips-desc">截图需包含评价内容、商品信息和账号昵称，避免被拒绝。</text>
        </view>
      </view>
    </scroll-view>

    <view class="bottom-actions">
      <u-button text="打开抖音去评价" shape="circle" :customStyle="outlineButtonStyle" @click="openDouyin"></u-button>
      <u-button type="primary" text="我已评价" shape="circle" :customStyle="primaryButtonStyle" @click="goSubmit"></u-button>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad, onPullDownRefresh } from '@dcloudio/uni-app'
import { demoActivity, getActivityByCode, reportCopyBehavior, startTask } from '@/api/task.js'

const code = ref('DP20260611')
const taskId = ref('')
const activity = ref({ ...demoActivity })
const selectedCopyIndex = ref(0)
const refreshing = ref(false)

const copywritingList = computed(() => activity.value.copywritingList || [])
const selectedCopy = computed(() => copywritingList.value[selectedCopyIndex.value] || copywritingList.value[0] || { content: '' })

const primaryButtonStyle = {
  height: '88rpx',
  fontSize: '30rpx',
  fontWeight: '700',
  background: 'linear-gradient(135deg,#1b6cff,#0f55ee)',
  border: '0'
}

const outlineButtonStyle = {
  height: '88rpx',
  fontSize: '30rpx',
  fontWeight: '700',
  color: '#1463ff',
  background: '#edf4ff',
  border: '0'
}

async function loadActivity() {
  try {
    const data = await getActivityByCode(code.value)
    activity.value = {
      ...demoActivity,
      ...data,
      copywritingList: data.copywritingList || data.textList || demoActivity.copywritingList,
      recommendImages: data.recommendImages || data.imageList || demoActivity.recommendImages
    }
  } catch (error) {
    activity.value = { ...demoActivity, code: code.value }
  }

  try {
    const started = await startTask(activity.value.id)
    taskId.value = started.taskId || started.id || taskId.value
  } catch (error) {
    taskId.value = taskId.value || `mock_${activity.value.id}`
  }
}

async function refreshData() {
  refreshing.value = true
  await loadActivity()
  refreshing.value = false
  uni.stopPullDownRefresh()
}

function copyText() {
  const item = selectedCopy.value
  uni.setClipboardData({
    data: item.content,
    success: async () => {
      uni.showToast({ title: '文案已复制', icon: 'success' })
      try {
        await reportCopyBehavior(taskId.value, item.id)
      } catch (error) {}
    }
  })
}

function previewImage(index) {
  uni.previewImage({
    current: index,
    urls: activity.value.recommendImages
  })
}

function saveImage(imageUrl) {
  // H5 无相册权限接口时使用预览兜底，App/小程序端可继续触发系统保存。
  uni.downloadFile({
    url: imageUrl,
    success: (res) => {
      uni.saveImageToPhotosAlbum({
        filePath: res.tempFilePath,
        success: () => uni.showToast({ title: '图片已保存', icon: 'success' }),
        fail: () => previewImage(activity.value.recommendImages.indexOf(imageUrl))
      })
    },
    fail: () => previewImage(activity.value.recommendImages.indexOf(imageUrl))
  })
}

function openDouyin() {
  if (!activity.value.douyinLink) {
    uni.showToast({ title: '暂无跳转链接', icon: 'none' })
    return
  }
  // #ifdef H5
  window.open(activity.value.douyinLink, '_blank')
  // #endif
  // #ifndef H5
  plus.runtime.openURL(activity.value.douyinLink)
  // #endif
}

function goSubmit() {
  uni.navigateTo({
    url: `/pages/task/submit?id=${taskId.value}&activityId=${activity.value.id}`
  })
}

function goCenter() {
  uni.switchTab({ url: '/pages/user/center' })
}

onLoad((options) => {
  code.value = options?.code || code.value
  loadActivity()
})

onPullDownRefresh(refreshData)
</script>

<style lang="scss" scoped>
.task-page {
  padding-bottom: 250rpx;
}

.hero {
  padding-bottom: 72rpx;
  background: linear-gradient(155deg, #0d5cff 0%, #145dff 52%, #2d86ff 100%);
  border-bottom-left-radius: 44rpx;
  border-bottom-right-radius: 44rpx;
  overflow: hidden;
}

.brand-dot {
  width: 52rpx;
  height: 52rpx;
  border-radius: 16rpx;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
}

.hero-card {
  margin: 16rpx 28rpx 0;
  padding: 28rpx;
  border-radius: 28rpx;
  background: rgba(255, 255, 255, 0.16);
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: #fff;
}

.hero-label,
.hero-desc,
.section-subtitle,
.product-desc,
.tips-desc {
  display: block;
  font-size: 24rpx;
  line-height: 36rpx;
}

.hero-label,
.hero-desc {
  color: rgba(255, 255, 255, 0.82);
}

.hero-title {
  display: block;
  margin: 14rpx 0;
  max-width: 420rpx;
  font-size: 38rpx;
  line-height: 48rpx;
  font-weight: 800;
}

.hero-image {
  width: 150rpx;
  height: 150rpx;
  border-radius: 24rpx;
  box-shadow: 0 16rpx 36rpx rgba(0, 0, 0, 0.18);
}

.content {
  height: calc(100vh - 310rpx);
  margin-top: -48rpx;
  padding: 0 28rpx;
  box-sizing: border-box;
}

.progress-card,
.section-card,
.tips-card {
  margin-bottom: 24rpx;
  background: #fff;
  border-radius: 28rpx;
  box-shadow: 0 16rpx 42rpx rgba(21, 48, 105, 0.08);
}

.progress-card {
  padding: 28rpx 20rpx;
  display: flex;
  align-items: center;
}

.progress-step {
  width: 150rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10rpx;
  color: #98a2b3;
  font-size: 23rpx;
}

.progress-step.active {
  color: #1463ff;
  font-weight: 700;
}

.step-index {
  width: 40rpx;
  height: 40rpx;
  border-radius: 50%;
  background: #edf4ff;
  color: #1463ff;
  text-align: center;
  line-height: 40rpx;
}

.progress-line {
  flex: 1;
  height: 2rpx;
  background: #edf0f5;
}

.section-card {
  padding: 28rpx;
}

.section-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 24rpx;
}

.section-title {
  display: block;
  font-size: 32rpx;
  line-height: 42rpx;
  font-weight: 800;
  color: #20263a;
}

.section-subtitle,
.product-desc,
.tips-desc {
  color: #98a2b3;
}

.product-row {
  display: flex;
  gap: 22rpx;
}

.product-image {
  width: 164rpx;
  height: 164rpx;
  border-radius: 22rpx;
}

.product-info {
  flex: 1;
}

.product-name {
  display: block;
  font-size: 30rpx;
  font-weight: 700;
  color: #20263a;
  margin-bottom: 10rpx;
}

.product-meta {
  margin-top: 18rpx;
  display: flex;
  align-items: center;
  gap: 8rpx;
  color: #98a2b3;
  font-size: 23rpx;
}

.count-text {
  color: #1463ff;
  font-size: 24rpx;
  font-weight: 700;
}

.copy-tabs {
  display: flex;
  gap: 14rpx;
  margin-bottom: 18rpx;
}

.copy-tab {
  padding: 14rpx 20rpx;
  border-radius: 999rpx;
  background: #f4f6fb;
  color: #626b82;
  font-size: 24rpx;
}

.copy-tab.active {
  color: #1463ff;
  background: #edf4ff;
  font-weight: 700;
}

.copy-box {
  min-height: 188rpx;
  padding: 24rpx;
  margin-bottom: 24rpx;
  border-radius: 22rpx;
  background: #f8faff;
  color: #20263a;
  font-size: 27rpx;
  line-height: 44rpx;
}

.image-scroll {
  white-space: nowrap;
}

.image-list {
  display: inline-flex;
  gap: 18rpx;
}

.image-item {
  position: relative;
}

.recommend-image {
  width: 210rpx;
  height: 210rpx;
  border-radius: 24rpx;
}

.save-chip {
  position: absolute;
  right: 12rpx;
  bottom: 12rpx;
  padding: 8rpx 14rpx;
  border-radius: 999rpx;
  background: rgba(0, 0, 0, 0.52);
  color: #fff;
  display: flex;
  align-items: center;
  gap: 6rpx;
  font-size: 22rpx;
}

.tips-card {
  padding: 24rpx;
  display: flex;
  gap: 18rpx;
}

.tips-title {
  display: block;
  color: #20263a;
  font-size: 28rpx;
  font-weight: 700;
}

.bottom-actions {
  position: fixed;
  left: 0;
  right: 0;
  bottom: calc(100rpx + env(safe-area-inset-bottom));
  padding: 18rpx 28rpx calc(18rpx + env(safe-area-inset-bottom));
  background: rgba(255, 255, 255, 0.96);
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18rpx;
  box-shadow: 0 -14rpx 36rpx rgba(21, 48, 105, 0.08);
  z-index: 20;
}

/* #ifdef H5 */
@media screen and (min-width: 480px) {
  .bottom-actions {
    left: 50%;
    right: auto;
    width: 430px;
    transform: translateX(-50%);
    box-sizing: border-box;
  }
}
/* #endif */
</style>

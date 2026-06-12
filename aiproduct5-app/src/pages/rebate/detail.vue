<template>
  <view class="dp-page rebate-page dp-safe-bottom">
    <u-navbar title="返利详情" bgColor="#f6f8fc" titleStyle="font-weight:700;color:#20263a" @leftClick="goBack"></u-navbar>

    <scroll-view scroll-y class="content">
      <view class="amount-card" :class="detail.status">
        <text class="amount-label">返利金额</text>
        <text class="amount-value">¥{{ detail.amount }}</text>
        <text class="status-pill">{{ detail.statusText }}</text>
      </view>

      <view class="section-card">
        <text class="section-title">关联任务</text>
        <view class="product-row">
          <image class="product-image" :src="detail.productImage" mode="aspectFill"></image>
          <view class="product-info">
            <text class="product-name">{{ detail.productName }}</text>
            <text class="product-desc">{{ detail.activityTitle }}</text>
            <text class="time-text">提交时间：{{ detail.submitTime }}</text>
          </view>
        </view>
      </view>

      <view class="section-card">
        <text class="section-title">审核进度</text>
        <view class="timeline-item active">
          <view class="dot"></view>
          <view>
            <text class="timeline-title">提交评价截图</text>
            <text class="timeline-desc">{{ detail.submitTime }}</text>
          </view>
        </view>
        <view class="timeline-item" :class="{ active: detail.status !== 'auditing' }">
          <view class="dot"></view>
          <view>
            <text class="timeline-title">{{ detail.status === 'rejected' ? '审核未通过' : '商家审核通过' }}</text>
            <text class="timeline-desc">{{ auditDescription }}</text>
          </view>
        </view>
        <view class="timeline-item" :class="{ active: detail.status === 'paid' }">
          <view class="dot"></view>
          <view>
            <text class="timeline-title">返利到账</text>
            <text class="timeline-desc">{{ detail.paidTime || '审核通过后等待到账' }}</text>
          </view>
        </view>
      </view>

      <view v-if="detail.rejectReason" class="reject-card">
        <view class="reject-title">
          <u-icon name="warning-fill" color="#ff5a5f" size="18"></u-icon>
          <text>拒绝原因</text>
        </view>
        <text class="reject-desc">{{ detail.rejectReason }}</text>
        <u-button type="primary" text="重新提交截图" shape="circle" :customStyle="primaryButtonStyle" @click="resubmit"></u-button>
      </view>

      <view class="section-card">
        <view class="section-head">
          <text class="section-title">评价截图</text>
          <text class="preview-link" @click="previewScreenshot">查看大图</text>
        </view>
        <image class="screenshot" :src="detail.screenshotUrl" mode="aspectFill" @click="previewScreenshot"></image>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { demoActivity, demoTasks, getRebateDetail } from '@/api/task.js'

const rebateId = ref('')
const detail = ref({
  rebateId: demoTasks[0].rebateId,
  taskId: demoTasks[0].taskId,
  amount: demoTasks[0].rebateAmount,
  status: demoTasks[0].status,
  statusText: demoTasks[0].statusText,
  activityTitle: demoTasks[0].activityTitle,
  productName: demoTasks[0].productName,
  productImage: demoTasks[0].productImage,
  submitTime: demoTasks[0].submitTime,
  paidTime: '',
  rejectReason: '',
  screenshotUrl: demoActivity.recommendImages[0]
})

const auditDescription = computed(() => {
  if (detail.value.status === 'auditing') return '商家正在核验截图，请耐心等待'
  if (detail.value.status === 'rejected') return detail.value.rejectReason || '截图不符合活动要求'
  return '审核通过，返利进入到账流程'
})

const primaryButtonStyle = {
  height: '84rpx',
  marginTop: '22rpx',
  fontWeight: '700',
  background: '#1463ff',
  border: '0'
}

async function loadDetail() {
  const demo = demoTasks.find((task) => String(task.rebateId) === String(rebateId.value)) || demoTasks[0]
  detail.value = {
    ...detail.value,
    ...demo,
    amount: demo.rebateAmount,
    screenshotUrl: demo.screenshotUrl || demoActivity.recommendImages[0]
  }
  try {
    const data = await getRebateDetail(rebateId.value)
    detail.value = {
      ...detail.value,
      ...data,
      amount: data.amount ?? data.rebateAmount ?? detail.value.amount,
      screenshotUrl: data.screenshotUrl || data.reviewImage || detail.value.screenshotUrl
    }
  } catch (error) {}
}

function previewScreenshot() {
  uni.previewImage({
    urls: [detail.value.screenshotUrl]
  })
}

function resubmit() {
  uni.navigateTo({ url: `/pages/task/submit?id=${detail.value.taskId}` })
}

function goBack() {
  uni.navigateBack()
}

onLoad((options) => {
  rebateId.value = options?.id || demoTasks[0].rebateId
  loadDetail()
})
</script>

<style lang="scss" scoped>
.content {
  height: calc(100vh - 96rpx);
  padding: 20rpx 28rpx 40rpx;
  box-sizing: border-box;
}

.amount-card,
.section-card,
.reject-card {
  margin-bottom: 24rpx;
  border-radius: 30rpx;
  background: #fff;
  box-shadow: 0 16rpx 42rpx rgba(21, 48, 105, 0.08);
}

.amount-card {
  padding: 40rpx 30rpx;
  text-align: center;
  background: linear-gradient(150deg, #1463ff, #2e88ff);
  color: #fff;
}

.amount-card.paid {
  background: linear-gradient(150deg, #20b968, #42d788);
}

.amount-card.rejected {
  background: linear-gradient(150deg, #ff5a5f, #ff8a78);
}

.amount-label {
  display: block;
  color: rgba(255, 255, 255, 0.74);
  font-size: 24rpx;
}

.amount-value {
  display: block;
  margin: 12rpx 0 20rpx;
  font-size: 58rpx;
  line-height: 68rpx;
  font-weight: 900;
}

.status-pill {
  display: inline-block;
  padding: 10rpx 22rpx;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.18);
  font-size: 24rpx;
  font-weight: 700;
}

.section-card,
.reject-card {
  padding: 28rpx;
}

.section-title {
  display: block;
  margin-bottom: 24rpx;
  color: #20263a;
  font-size: 32rpx;
  font-weight: 800;
}

.product-row {
  display: flex;
  gap: 20rpx;
}

.product-image {
  width: 142rpx;
  height: 142rpx;
  border-radius: 22rpx;
}

.product-info {
  flex: 1;
}

.product-name {
  display: block;
  color: #20263a;
  font-size: 30rpx;
  font-weight: 800;
}

.product-desc,
.time-text,
.timeline-desc,
.reject-desc {
  display: block;
  color: #98a2b3;
  font-size: 24rpx;
  line-height: 36rpx;
}

.product-desc {
  margin-top: 8rpx;
}

.time-text {
  margin-top: 14rpx;
}

.timeline-item {
  position: relative;
  display: flex;
  gap: 18rpx;
  padding: 18rpx 0 26rpx 8rpx;
}

.timeline-item::before {
  content: '';
  position: absolute;
  left: 17rpx;
  top: 54rpx;
  bottom: -16rpx;
  width: 2rpx;
  background: #edf0f5;
}

.timeline-item:last-child::before {
  display: none;
}

.dot {
  width: 20rpx;
  height: 20rpx;
  margin-top: 8rpx;
  border-radius: 50%;
  background: #c8d0df;
}

.timeline-item.active .dot {
  background: #1463ff;
  box-shadow: 0 0 0 10rpx #edf4ff;
}

.timeline-title {
  display: block;
  color: #20263a;
  font-size: 28rpx;
  font-weight: 800;
}

.reject-card {
  background: #fff8f8;
}

.reject-title {
  display: flex;
  align-items: center;
  gap: 10rpx;
  color: #ff5a5f;
  font-size: 30rpx;
  font-weight: 800;
  margin-bottom: 12rpx;
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.preview-link {
  color: #1463ff;
  font-size: 24rpx;
  font-weight: 700;
}

.screenshot {
  width: 100%;
  height: 360rpx;
  border-radius: 24rpx;
}
</style>

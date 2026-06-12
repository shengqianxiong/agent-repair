<template>
  <view class="dp-page result-page dp-safe-bottom">
    <u-navbar title="提交结果" bgColor="#f6f8fc" titleStyle="font-weight:700;color:#20263a" :autoBack="false"></u-navbar>

    <view class="result-card">
      <view class="success-icon">
        <u-icon name="checkmark" color="#fff" size="42"></u-icon>
      </view>
      <text class="result-title">评价提交成功！</text>
      <text class="result-desc">商家将在 1-2 个工作日内完成审核，审核通过后返利进入到账流程。</text>
      <view class="amount-box">
        <text class="amount-label">预计返利</text>
        <text class="amount-value">¥{{ rebateAmount }}</text>
      </view>
    </view>

    <view class="timeline-card">
      <text class="section-title">审核进度</text>
      <view class="timeline-item active">
        <view class="dot"></view>
        <view>
          <text class="timeline-title">已提交截图</text>
          <text class="timeline-desc">审核单号：{{ auditId }}</text>
        </view>
      </view>
      <view class="timeline-item">
        <view class="dot"></view>
        <view>
          <text class="timeline-title">商家审核</text>
          <text class="timeline-desc">通过后返利状态将同步更新</text>
        </view>
      </view>
      <view class="timeline-item">
        <view class="dot"></view>
        <view>
          <text class="timeline-title">返利到账</text>
          <text class="timeline-desc">可在个人中心查看累计返利</text>
        </view>
      </view>
    </view>

    <view class="action-group">
      <u-button type="primary" text="查看评价记录" shape="circle" :customStyle="primaryButtonStyle" @click="goRecords"></u-button>
      <u-button text="返回个人中心" shape="circle" :customStyle="outlineButtonStyle" @click="goCenter"></u-button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { demoActivity } from '@/api/task.js'

const auditId = ref('mock_audit')
const rebateAmount = ref(demoActivity.rebateAmount)

const primaryButtonStyle = {
  height: '92rpx',
  fontSize: '31rpx',
  fontWeight: '700',
  background: 'linear-gradient(135deg,#1b6cff,#0f55ee)',
  border: '0'
}

const outlineButtonStyle = {
  height: '92rpx',
  fontSize: '31rpx',
  fontWeight: '700',
  color: '#1463ff',
  background: '#edf4ff',
  border: '0'
}

function goRecords() {
  uni.switchTab({ url: '/pages/user/records' })
}

function goCenter() {
  uni.switchTab({ url: '/pages/user/center' })
}

onLoad((options) => {
  auditId.value = options?.auditId || auditId.value
})
</script>

<style lang="scss" scoped>
.result-page {
  padding: 24rpx 28rpx;
}

.result-card,
.timeline-card {
  border-radius: 32rpx;
  background: #fff;
  box-shadow: 0 16rpx 42rpx rgba(21, 48, 105, 0.08);
}

.result-card {
  padding: 52rpx 36rpx 36rpx;
  text-align: center;
}

.success-icon {
  width: 112rpx;
  height: 112rpx;
  margin: 0 auto 28rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #25c06d, #43d78a);
  display: flex;
  align-items: center;
  justify-content: center;
}

.result-title {
  display: block;
  color: #20263a;
  font-size: 38rpx;
  line-height: 48rpx;
  font-weight: 800;
}

.result-desc {
  display: block;
  margin-top: 18rpx;
  color: #626b82;
  font-size: 26rpx;
  line-height: 42rpx;
}

.amount-box {
  margin-top: 34rpx;
  padding: 28rpx;
  border-radius: 24rpx;
  background: #fff8ed;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.amount-label {
  color: #8a6a35;
  font-size: 26rpx;
}

.amount-value {
  color: #ff8a00;
  font-size: 42rpx;
  font-weight: 900;
}

.timeline-card {
  margin-top: 24rpx;
  padding: 30rpx;
}

.section-title {
  display: block;
  margin-bottom: 12rpx;
  color: #20263a;
  font-size: 32rpx;
  font-weight: 800;
}

.timeline-item {
  position: relative;
  display: flex;
  gap: 18rpx;
  padding: 24rpx 0 24rpx 8rpx;
}

.timeline-item::before {
  content: '';
  position: absolute;
  left: 17rpx;
  top: 58rpx;
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
  font-weight: 700;
}

.timeline-desc {
  display: block;
  margin-top: 8rpx;
  color: #98a2b3;
  font-size: 24rpx;
}

.action-group {
  margin-top: 32rpx;
  display: grid;
  gap: 18rpx;
}
</style>

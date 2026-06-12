<template>
  <view class="dp-page submit-page dp-safe-bottom">
    <u-navbar title="提交评价" bgColor="#f6f8fc" titleStyle="font-weight:700;color:#20263a" @leftClick="goBack"></u-navbar>

    <scroll-view scroll-y class="content">
      <view class="status-card">
        <view class="status-icon">
          <u-icon name="camera-fill" color="#1463ff" size="30"></u-icon>
        </view>
        <view>
          <text class="status-title">上传评价截图</text>
          <text class="status-desc">请确保截图包含评价内容、商品信息与账号昵称。</text>
        </view>
      </view>

      <view class="section-card">
        <view class="section-header">
          <text class="section-title">任务信息</text>
          <u-tag text="待提交" type="primary" size="mini" plain></u-tag>
        </view>
        <view class="product-row">
          <image class="product-image" :src="detail.productImage" mode="aspectFill"></image>
          <view class="product-info">
            <text class="product-name">{{ detail.productName }}</text>
            <text class="product-desc">{{ detail.activityTitle }}</text>
            <text class="rebate-text">预计返利 ¥{{ detail.rebateAmount }}</text>
          </view>
        </view>
      </view>

      <view class="section-card">
        <view class="section-header">
          <text class="section-title">评价截图</text>
          <text class="required-text">必填</text>
        </view>
        <view v-if="screenshotUrl" class="screenshot-box" @click="previewScreenshot">
          <image class="screenshot-image" :src="screenshotUrl" mode="aspectFill"></image>
          <view class="replace-mask" @click.stop="chooseScreenshot">重新上传</view>
        </view>
        <view v-else class="upload-box" @click="chooseScreenshot">
          <u-icon name="plus-circle-fill" color="#1463ff" size="38"></u-icon>
          <text>上传评价截图</text>
          <text class="upload-tips">支持相册选择或拍照</text>
        </view>
      </view>

      <view class="section-card form-card">
        <view class="section-header">
          <text class="section-title">补充信息</text>
        </view>
        <view class="field">
          <text class="field-label">联系方式</text>
          <input v-model="form.contact" class="field-input" placeholder="手机号/微信号，便于异常沟通" maxlength="40" />
        </view>
        <view class="field textarea-field">
          <text class="field-label">备注说明</text>
          <textarea v-model="form.remark" class="field-textarea" placeholder="可补充评价账号昵称、订单尾号等信息" maxlength="200" />
          <text class="counter">{{ form.remark.length }}/200</text>
        </view>
      </view>

      <view class="check-card">
        <text class="check-title">提交前请确认</text>
        <view class="check-row" v-for="item in checkItems" :key="item">
          <u-icon name="checkbox-mark" color="#25c06d" size="16"></u-icon>
          <text>{{ item }}</text>
        </view>
      </view>
    </scroll-view>

    <view class="bottom-action">
      <u-button type="primary" text="提交返利审核" shape="circle" :loading="submitting" :customStyle="primaryButtonStyle" @click="openConfirm"></u-button>
    </view>

    <u-modal
      :show="confirmVisible"
      title="确认提交审核？"
      content="提交后商家会核验评价截图，审核通过后返利会进入待到账流程。"
      showCancelButton
      confirmText="确认提交"
      @cancel="confirmVisible = false"
      @confirm="submitReview"
    ></u-modal>
  </view>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { demoActivity, demoTasks, getTaskDetail, submitTask } from '@/api/task.js'

const taskId = ref('')
const activityId = ref('')
const screenshotUrl = ref('')
const submitting = ref(false)
const confirmVisible = ref(false)
const detail = ref({
  ...demoTasks[0],
  activityTitle: demoActivity.title,
  productName: demoActivity.productName,
  productImage: demoActivity.productImage,
  rebateAmount: demoActivity.rebateAmount
})

const form = reactive({
  contact: '',
  remark: ''
})

const checkItems = ['截图清晰展示评价正文', '评价内容与复制文案一致或合理改写', '商品和账号信息未被遮挡']

const primaryButtonStyle = {
  height: '92rpx',
  fontSize: '31rpx',
  fontWeight: '700',
  background: 'linear-gradient(135deg,#1b6cff,#0f55ee)',
  border: '0'
}

async function loadDetail() {
  if (!taskId.value) return
  try {
    const data = await getTaskDetail(taskId.value)
    detail.value = {
      ...detail.value,
      ...data,
      productImage: data.productImage || data.activityInfo?.productImage || detail.value.productImage,
      productName: data.productName || data.activityInfo?.productName || detail.value.productName,
      activityTitle: data.activityTitle || data.activityInfo?.title || detail.value.activityTitle,
      rebateAmount: data.rebateAmount || data.activityInfo?.rebateAmount || detail.value.rebateAmount
    }
  } catch (error) {}
}

function chooseScreenshot() {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: (res) => {
      screenshotUrl.value = res.tempFilePaths[0]
    }
  })
}

function previewScreenshot() {
  uni.previewImage({
    urls: [screenshotUrl.value]
  })
}

function openConfirm() {
  if (!screenshotUrl.value) {
    uni.showToast({ title: '请先上传评价截图', icon: 'none' })
    return
  }
  confirmVisible.value = true
}

async function submitReview() {
  confirmVisible.value = false
  submitting.value = true
  try {
    const data = await submitTask({
      taskId: taskId.value,
      activityId: activityId.value,
      screenshotUrl: screenshotUrl.value,
      remark: form.remark,
      contact: form.contact
    })
    const auditId = data.auditId || data.id || 'mock_audit'
    uni.redirectTo({ url: `/pages/task/result?auditId=${auditId}&taskId=${taskId.value}` })
  } catch (error) {
    uni.redirectTo({ url: `/pages/task/result?auditId=mock_audit&taskId=${taskId.value}` })
  } finally {
    submitting.value = false
  }
}

function goBack() {
  uni.navigateBack()
}

onLoad((options) => {
  taskId.value = options?.id || ''
  activityId.value = options?.activityId || ''
  loadDetail()
})
</script>

<style lang="scss" scoped>
.submit-page {
  padding-bottom: 142rpx;
}

.content {
  height: calc(100vh - 122rpx);
  padding: 20rpx 28rpx 0;
  box-sizing: border-box;
}

.status-card,
.section-card,
.check-card {
  margin-bottom: 24rpx;
  border-radius: 28rpx;
  background: #fff;
  box-shadow: 0 16rpx 42rpx rgba(21, 48, 105, 0.08);
}

.status-card {
  padding: 28rpx;
  display: flex;
  gap: 22rpx;
  align-items: center;
}

.status-icon {
  width: 76rpx;
  height: 76rpx;
  border-radius: 24rpx;
  background: #edf4ff;
  display: flex;
  align-items: center;
  justify-content: center;
}

.status-title,
.section-title,
.check-title {
  display: block;
  font-size: 32rpx;
  line-height: 42rpx;
  color: #20263a;
  font-weight: 800;
}

.status-desc,
.product-desc,
.upload-tips {
  display: block;
  color: #98a2b3;
  font-size: 24rpx;
  line-height: 36rpx;
}

.section-card,
.check-card {
  padding: 28rpx;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 22rpx;
}

.product-row {
  display: flex;
  gap: 22rpx;
}

.product-image {
  width: 148rpx;
  height: 148rpx;
  border-radius: 24rpx;
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

.rebate-text {
  display: inline-block;
  margin-top: 18rpx;
  padding: 8rpx 16rpx;
  border-radius: 999rpx;
  color: #ff8a00;
  background: #fff6e8;
  font-size: 24rpx;
  font-weight: 700;
}

.required-text {
  color: #ff5a5f;
  font-size: 24rpx;
}

.upload-box,
.screenshot-box {
  height: 350rpx;
  border-radius: 24rpx;
  overflow: hidden;
}

.upload-box {
  border: 2rpx dashed #cfe0ff;
  background: #f8faff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  color: #1463ff;
  font-size: 28rpx;
  font-weight: 700;
}

.screenshot-box {
  position: relative;
}

.screenshot-image {
  width: 100%;
  height: 100%;
}

.replace-mask {
  position: absolute;
  right: 18rpx;
  bottom: 18rpx;
  padding: 12rpx 22rpx;
  border-radius: 999rpx;
  color: #fff;
  background: rgba(0, 0, 0, 0.58);
  font-size: 24rpx;
}

.field {
  padding: 22rpx 0;
  border-bottom: 1rpx solid #edf0f5;
}

.field:last-child {
  border-bottom: 0;
}

.field-label {
  display: block;
  margin-bottom: 14rpx;
  color: #20263a;
  font-size: 27rpx;
  font-weight: 700;
}

.field-input,
.field-textarea {
  width: 100%;
  color: #20263a;
  font-size: 27rpx;
}

.field-input {
  height: 64rpx;
}

.textarea-field {
  position: relative;
}

.field-textarea {
  min-height: 152rpx;
  line-height: 40rpx;
}

.counter {
  position: absolute;
  right: 0;
  bottom: 20rpx;
  color: #98a2b3;
  font-size: 22rpx;
}

.check-row {
  margin-top: 18rpx;
  display: flex;
  align-items: center;
  gap: 12rpx;
  color: #626b82;
  font-size: 25rpx;
}

.bottom-action {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 20rpx 28rpx calc(20rpx + env(safe-area-inset-bottom));
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 -14rpx 36rpx rgba(21, 48, 105, 0.08);
}

/* #ifdef H5 */
@media screen and (min-width: 480px) {
  .bottom-action {
    left: 50%;
    right: auto;
    width: 430px;
    transform: translateX(-50%);
    box-sizing: border-box;
  }
}
/* #endif */
</style>

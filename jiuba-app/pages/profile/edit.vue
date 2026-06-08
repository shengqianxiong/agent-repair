<template>
  <view class="page">
    <LoadingOverlay :show="loading" />
    <AppNavbar title="编辑资料" right-icon="more-dot-fill" bg-color="#FFFFFF" />

    <scroll-view scroll-y class="content">
      <view class="avatar-section" @click="changeAvatar">
        <view class="avatar-wrap">
          <image class="avatar" :src="form.avatar" mode="aspectFill" />
          <view class="camera-icon"><u-icon name="camera-fill" color="#FFF" size="16"></u-icon></view>
        </view>
        <text class="avatar-tip">点击更换头像</text>
      </view>

      <view class="form-group">
        <text class="form-label">昵称</text>
        <input v-model="form.nickname" class="form-input" placeholder="请输入昵称" />
      </view>

      <view class="form-group">
        <text class="form-label">性别</text>
        <view class="gender-row">
          <view class="gender-btn" :class="{ active: form.gender === 1 }" @click="form.gender = 1">男</view>
          <view class="gender-btn" :class="{ active: form.gender === 2 }" @click="form.gender = 2">女</view>
        </view>
      </view>

      <view class="form-row">
        <view class="form-group half">
          <text class="form-label">年龄</text>
          <picker :value="ageIndex" :range="ageRange" @change="onAgeChange">
            <view class="form-input picker">{{ form.age }}岁</view>
          </picker>
        </view>
        <view class="form-group half">
          <text class="form-label">联系电话</text>
          <input v-model="form.phone" class="form-input" type="number" placeholder="手机号" maxlength="11" />
        </view>
      </view>

      <view class="switch-card">
        <text>附近好友展示</text>
        <u-switch v-model="form.showNearby" activeColor="#7B61FF"></u-switch>
      </view>

      <view class="form-group">
        <text class="form-label">个人简介</text>
        <textarea v-model="form.bio" class="form-textarea" placeholder="写点什么介绍自己..." maxlength="200" />
      </view>

      <view class="form-group">
        <text class="form-label">个人相册</text>
        <view class="upload-area" @click="showToast('上传相册')">
          <u-icon name="plus" color="#CCC" size="24"></u-icon>
          <text>添加图片</text>
        </view>
      </view>

      <view class="bottom-spacer"></view>
    </scroll-view>

    <view class="submit-bar">
      <view class="submit-btn" @click="save">保存修改</view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getUserInfo, updateUserProfile } from '@/api/index.js'
import { showToast, showLoading, hideLoading, validatePhone, validateRequired } from '@/utils/common.js'

const loading = ref(false)
const form = ref({
  avatar: '', nickname: '', gender: 1, age: 26, phone: '', showNearby: true, bio: ''
})

const ageRange = Array.from({ length: 63 }, (_, i) => i + 18)
const ageIndex = computed(() => ageRange.indexOf(form.value.age))

async function loadUser() {
  loading.value = true
  try {
    const user = await getUserInfo()
    if (user) form.value = { ...form.value, ...user }
  } finally { loading.value = false }
}

function onAgeChange(e) { form.value.age = ageRange[e.detail.value] }
function changeAvatar() { showToast('选择头像') }

async function save() {
  if (!validateRequired(form.value.nickname, '昵称')) return
  if (form.value.phone && !validatePhone(form.value.phone)) { showToast('手机号格式不正确'); return }
  showLoading('保存中...')
  try {
    await updateUserProfile(form.value)
    hideLoading()
    showToast('保存成功')
    setTimeout(() => uni.navigateBack(), 500)
  } catch (e) { hideLoading() }
}

onMounted(loadUser)
</script>

<style lang="scss" scoped>
.page { min-height: 100vh; background: #F8F9FB; }
.content { padding: 32rpx; height: calc(100vh - 200rpx); }
.avatar-section { display: flex; flex-direction: column; align-items: center; margin-bottom: 40rpx; }
.avatar-wrap { position: relative; }
.avatar { width: 200rpx; height: 200rpx; border-radius: 50%; }
.camera-icon { position: absolute; right: 8rpx; bottom: 8rpx; width: 48rpx; height: 48rpx; border-radius: 50%; background: #7B61FF; display: flex; align-items: center; justify-content: center; }
.avatar-tip { font-size: 26rpx; color: #999; margin-top: 16rpx; }
.form-group { margin-bottom: 28rpx;
  &.half { flex: 1; }
}
.form-label { font-size: 28rpx; color: #666; display: block; margin-bottom: 12rpx; }
.form-input {
  background: #F7F7F7; border-radius: 24rpx; padding: 24rpx 28rpx; font-size: 28rpx;
  &.picker { color: #333; }
}
.form-row { display: flex; gap: 24rpx; }
.gender-row { display: flex; gap: 24rpx; }
.gender-btn {
  flex: 1; text-align: center; padding: 20rpx; border-radius: 24rpx; background: #F7F7F7; font-size: 28rpx; color: #666;
  &.active { background: #7B61FF; color: #FFF; }
}
.switch-card {
  display: flex; justify-content: space-between; align-items: center; background: #FFF;
  border-radius: 24rpx; padding: 28rpx 32rpx; margin-bottom: 28rpx; font-size: 28rpx;
}
.form-textarea { background: #F7F7F7; border-radius: 24rpx; padding: 24rpx; width: 100%; height: 240rpx; font-size: 28rpx; box-sizing: border-box; }
.upload-area {
  border: 2rpx dashed #DDD; border-radius: 24rpx; height: 160rpx; display: flex; flex-direction: column;
  align-items: center; justify-content: center; gap: 8rpx; color: #CCC; font-size: 26rpx;
}
.bottom-spacer { height: 140rpx; }
.submit-bar {
  position: fixed; left: 0; right: 0; bottom: 0; padding: 20rpx 32rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom)); background: #FFF;
}
.submit-btn {
  background: #7F66FF; color: #FFF; text-align: center; padding: 28rpx; border-radius: 30rpx;
  font-size: 32rpx; font-weight: 600; box-shadow: var(--shadow-button);
}
</style>

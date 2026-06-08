<template>
  <view class="page">
    <view class="header">
      <view class="logo-wrap">
        <u-icon name="bag" color="#FFFFFF" size="36"></u-icon>
      </view>
      <text class="title">VerifyDevPlan</text>
      <text class="subtitle">欢迎登录，探索精选商品</text>
    </view>

    <view class="form-card">
      <view class="field">
        <text class="label">用户名</text>
        <u-input
          v-model="form.username"
          placeholder="请输入用户名"
          border="none"
          clearable
          :custom-style="inputStyle"
        ></u-input>
      </view>
      <view class="divider"></view>
      <view class="field">
        <text class="label">密码</text>
        <u-input
          v-model="form.password"
          type="password"
          placeholder="请输入密码"
          border="none"
          clearable
          :custom-style="inputStyle"
        ></u-input>
      </view>
    </view>

    <u-button
      type="primary"
      text="登录"
      shape="circle"
      :loading="loading"
      :custom-style="buttonStyle"
      @click="handleLogin"
    ></u-button>

    <text class="hint">演示账号：demo / 123456</text>
  </view>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/user.js'
import { validateRequired, showToast } from '@/utils/common.js'

const userStore = useUserStore()
const loading = ref(false)

const form = reactive({
  username: 'demo',
  password: '123456'
})

const inputStyle = {
  backgroundColor: 'transparent',
  padding: '0',
  fontSize: '32rpx',
  color: '#333333'
}

const buttonStyle = {
  marginTop: '64rpx',
  height: '96rpx',
  background: 'linear-gradient(135deg, #7246F2 0%, #9D7BFF 100%)',
  boxShadow: '0 8rpx 24rpx rgba(123, 97, 255, 0.25)'
}

async function handleLogin() {
  if (!validateRequired(form.username, '用户名')) return
  if (!validateRequired(form.password, '密码')) return

  loading.value = true
  try {
    await userStore.login(form.username.trim(), form.password)
    showToast('登录成功', 'success')
    setTimeout(() => {
      uni.reLaunch({ url: '/pages/index/index' })
    }, 300)
  } catch (err) {
    console.error('login failed', err)
  } finally {
    loading.value = false
  }
}

onShow(() => {
  userStore.restoreSession()
  if (userStore.loggedIn) {
    uni.reLaunch({ url: '/pages/index/index' })
  }
})
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  padding: 120rpx var(--page-padding) var(--page-padding);
  background: var(--color-background);
}

.header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 80rpx;
}

.logo-wrap {
  width: 120rpx;
  height: 120rpx;
  border-radius: var(--radius-lg);
  background: var(--color-primary-gradient);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--shadow-button);
  margin-bottom: var(--spacing-base);
}

.title {
  font-size: var(--font-size-xl);
  font-weight: 600;
  color: var(--color-text-main);
  margin-bottom: var(--spacing-sm);
}

.subtitle {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.form-card {
  background: var(--color-card-bg);
  border-radius: var(--radius-lg);
  padding: 0 var(--spacing-base);
  box-shadow: var(--shadow-card);
}

.field {
  padding: var(--spacing-base) 0;
}

.label {
  display: block;
  font-size: var(--font-size-sm);
  color: var(--color-text-tertiary);
  margin-bottom: var(--spacing-sm);
}

.divider {
  height: 1rpx;
  background: var(--color-divider);
}

.hint {
  display: block;
  margin-top: var(--spacing-lg);
  text-align: center;
  font-size: 24rpx;
  color: var(--color-text-tertiary);
}
</style>

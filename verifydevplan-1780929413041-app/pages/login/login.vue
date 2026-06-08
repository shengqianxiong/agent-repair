<template>
  <view class="login-page">
    <view class="login-header">
      <view class="logo-wrap">
        <u-icon name="bag-fill" color="#FFFFFF" size="48"></u-icon>
      </view>
      <text class="app-title">{{ appName }}</text>
      <text class="app-subtitle">欢迎登录，探索精选商品</text>
    </view>

    <view class="login-card">
      <view class="form-item">
        <text class="form-label">用户名</text>
        <u-input
          v-model="form.username"
          placeholder="请输入用户名"
          border="none"
          clearable
          :custom-style="inputStyle"
        ></u-input>
      </view>
      <view class="form-divider"></view>
      <view class="form-item">
        <text class="form-label">密码</text>
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

    <view class="login-tip">
      <text>演示账号：demo / 123456</text>
    </view>

    <u-button
      type="primary"
      text="登 录"
      shape="circle"
      :loading="loading"
      :custom-style="buttonStyle"
      @click="handleLogin"
    ></u-button>
  </view>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import config from '@/config/index.js'
import { userLogin } from '@/api/user.js'
import { setToken, setUser, isLoggedIn } from '@/utils/auth.js'

const appName = config.appName
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const inputStyle = {
  backgroundColor: 'transparent',
  padding: '0',
  fontSize: '32rpx'
}

const buttonStyle = {
  height: '96rpx',
  fontSize: '32rpx',
  background: 'var(--color-primary-gradient)',
  boxShadow: 'var(--shadow-button)'
}

onShow(() => {
  if (isLoggedIn()) {
    uni.reLaunch({ url: '/pages/index/index' })
  }
})

function validateForm() {
  if (!form.username.trim()) {
    uni.showToast({ title: '请输入用户名', icon: 'none' })
    return false
  }
  if (!form.password.trim()) {
    uni.showToast({ title: '请输入密码', icon: 'none' })
    return false
  }
  return true
}

async function handleLogin() {
  if (!validateForm() || loading.value) return

  loading.value = true
  try {
    const data = await userLogin({
      username: form.username.trim(),
      password: form.password
    })
    setToken(data.token)
    setUser(data.userInfo)
    uni.showToast({ title: '登录成功', icon: 'success' })
    setTimeout(() => {
      uni.reLaunch({ url: '/pages/index/index' })
    }, 400)
  } catch {
    // 错误提示由 request 层处理
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  padding: calc(var(--spacing-xxl) + env(safe-area-inset-top)) var(--page-padding)
    calc(var(--spacing-xl) + env(safe-area-inset-bottom));
  box-sizing: border-box;
  background: var(--color-background);
}

.login-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: var(--section-gap);
}

.logo-wrap {
  width: 128rpx;
  height: 128rpx;
  border-radius: var(--radius-card);
  background: var(--color-primary-gradient);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--shadow-button);
  margin-bottom: var(--spacing-md);
}

.app-title {
  font-size: var(--font-size-xxl);
  font-weight: 600;
  color: var(--color-text-main);
  margin-bottom: var(--spacing-xs);
}

.app-subtitle {
  font-size: var(--font-size-sm);
  color: var(--color-text-tertiary);
}

.login-card {
  background: var(--color-card-bg);
  border-radius: var(--radius-card);
  padding: var(--spacing-base);
  box-shadow: var(--shadow-card);
  margin-bottom: var(--spacing-md);
}

.form-item {
  padding: var(--spacing-sm) 0;
}

.form-label {
  display: block;
  font-size: var(--font-size-xs);
  color: var(--color-text-tertiary);
  margin-bottom: var(--spacing-xs);
}

.form-divider {
  height: 1rpx;
  background: var(--color-divider);
  margin: var(--spacing-xs) 0;
}

.login-tip {
  text-align: center;
  font-size: var(--font-size-xs);
  color: var(--color-text-tertiary);
  margin-bottom: var(--spacing-xl);
}
</style>

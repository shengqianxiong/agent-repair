<template>
  <view class="login-page">
    <view class="login-header">
      <view class="logo-wrap">
        <u-icon name="account-fill" color="#2979ff" size="48"></u-icon>
      </view>
      <text class="title">用户登录</text>
      <text class="subtitle">请输入账号和密码登录系统</text>
    </view>

    <view class="login-form">
      <view class="form-item">
        <text class="form-label">账号</text>
        <u-input
          v-model="form.username"
          placeholder="请输入账号"
          clearable
          border="surround"
        ></u-input>
      </view>
      <view class="form-item">
        <text class="form-label">密码</text>
        <u-input
          v-model="form.password"
          type="password"
          placeholder="请输入密码"
          clearable
          border="surround"
        ></u-input>
      </view>

      <u-button
        type="primary"
        text="登 录"
        :loading="submitting"
        customStyle="margin-top: 48rpx; height: 88rpx;"
        @click="handleLogin"
      ></u-button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { accountLogin } from '@/api/modules/account.js'
import { isLoggedIn, setToken, setUserInfo } from '@/utils/auth.js'

const form = ref({
  username: '',
  password: ''
})
const submitting = ref(false)

onShow(() => {
  // 已登录且 Token 有效时直接进入首页
  if (isLoggedIn()) {
    uni.reLaunch({ url: '/pages/index/index' })
  }
})

/**
 * 前端空值校验，与服务端提示文案保持一致。
 */
function validateForm() {
  const username = form.value.username.trim()
  const password = form.value.password.trim()

  if (!username) {
    uni.showToast({ title: '请输入账号', icon: 'none' })
    return false
  }
  if (!password) {
    uni.showToast({ title: '请输入密码', icon: 'none' })
    return false
  }
  return true
}

async function handleLogin() {
  if (!validateForm() || submitting.value) {
    return
  }

  submitting.value = true
  try {
    const result = await accountLogin({
      username: form.value.username.trim(),
      password: form.value.password.trim()
    })

    setToken(result.token)
    setUserInfo({
      accountId: result.accountId,
      username: result.username,
      role: result.role
    })

    uni.showToast({ title: '登录成功', icon: 'success' })
    setTimeout(() => {
      uni.reLaunch({ url: '/pages/index/index' })
    }, 500)
  } catch (error) {
    // 登录失败时展示服务端返回的友好提示
    const message = error?.msg || '账号或密码错误'
    uni.showToast({ title: message, icon: 'none' })
  } finally {
    submitting.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  padding: 120rpx 48rpx 48rpx;
  background: linear-gradient(180deg, #e8f1ff 0%, #f5f7fa 40%);
}

.login-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 64rpx;
}

.logo-wrap {
  width: 120rpx;
  height: 120rpx;
  border-radius: 24rpx;
  background: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 24rpx rgba(41, 121, 255, 0.15);
  margin-bottom: 32rpx;
}

.title {
  font-size: 44rpx;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12rpx;
}

.subtitle {
  font-size: 26rpx;
  color: #909399;
}

.login-form {
  background: #ffffff;
  border-radius: 24rpx;
  padding: 40rpx 32rpx;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.06);
}

.form-item {
  margin-bottom: 32rpx;
}

.form-label {
  display: block;
  font-size: 28rpx;
  color: #606266;
  margin-bottom: 16rpx;
}
</style>

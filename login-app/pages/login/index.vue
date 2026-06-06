<template>
  <view class="login-page">
    <view class="login-header">
      <text class="login-title">用户登录</text>
      <text class="login-subtitle">请输入账号和密码登录系统</text>
    </view>

    <view class="login-form">
      <view class="form-item">
        <text class="form-label">账号</text>
        <u-input
          v-model="form.username"
          placeholder="请输入账号"
          clearable
          border="surround"
          :custom-style="inputStyle"
        />
      </view>

      <view class="form-item">
        <text class="form-label">密码</text>
        <u-input
          v-model="form.password"
          type="password"
          placeholder="请输入密码"
          clearable
          border="surround"
          :custom-style="inputStyle"
        />
      </view>

      <u-button
        type="primary"
        text="登录"
        :loading="submitting"
        :disabled="submitting"
        custom-style="margin-top: 48rpx; height: 88rpx; border-radius: 12rpx;"
        @click="handleLogin"
      />
    </view>
  </view>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { login } from '@/api/index.js'
import config from '@/config/index.js'
import { setToken, setUsername } from '@/utils/auth.js'

const form = reactive({
  username: '',
  password: ''
})

const submitting = ref(false)

const inputStyle = {
  backgroundColor: '#ffffff',
  borderRadius: '12rpx'
}

/**
 * 客户端表单校验
 */
function validateForm() {
  const username = form.username.trim()
  const password = form.password.trim()

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

/**
 * 提交登录请求，成功后跳转首页
 */
async function handleLogin() {
  if (!validateForm()) {
    return
  }

  submitting.value = true
  try {
    const data = await login({
      username: form.username.trim(),
      password: form.password.trim()
    })

    setToken(data.token, config.tokenExpireHours)
    setUsername(data.username || form.username.trim())

    uni.showToast({ title: '登录成功', icon: 'success' })
    setTimeout(() => {
      uni.reLaunch({ url: '/pages/home/index' })
    }, 500)
  } catch (error) {
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
  box-sizing: border-box;
  background: linear-gradient(180deg, #eff6ff 0%, #f3f4f6 40%);
}

.login-header {
  margin-bottom: 80rpx;
}

.login-title {
  display: block;
  font-size: 52rpx;
  font-weight: 600;
  color: #111827;
  margin-bottom: 16rpx;
}

.login-subtitle {
  display: block;
  font-size: 28rpx;
  color: #6b7280;
}

.login-form {
  background: #ffffff;
  border-radius: 24rpx;
  padding: 48rpx 32rpx;
  box-shadow: 0 8rpx 32rpx rgba(15, 23, 42, 0.06);
}

.form-item {
  margin-bottom: 32rpx;
}

.form-label {
  display: block;
  font-size: 28rpx;
  color: #374151;
  margin-bottom: 16rpx;
  font-weight: 500;
}
</style>

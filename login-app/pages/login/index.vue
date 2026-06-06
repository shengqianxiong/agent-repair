<template>
  <view class="login-page">
    <view class="login-header">
      <text class="login-title">用户登录</text>
      <text class="login-subtitle">请输入账号和密码登录系统</text>
    </view>

    <view class="login-card">
      <u-form :model="form" ref="formRef" label-position="top">
        <u-form-item label="账号" prop="username">
          <u-input
            v-model="form.username"
            placeholder="请输入账号"
            clearable
            border="surround"
          />
        </u-form-item>

        <u-form-item label="密码" prop="password">
          <u-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            clearable
            border="surround"
          />
        </u-form-item>
      </u-form>

      <u-button
        type="primary"
        text="登录"
        :loading="submitting"
        custom-style="margin-top: 48rpx;"
        @click="handleLogin"
      />
    </view>
  </view>
</template>

<script setup>
import { onShow } from '@dcloudio/uni-app'
import { reactive, ref } from 'vue'
import { login } from '@/api/modules/auth.js'
import { isLoggedIn, setToken, setUserInfo } from '@/utils/auth.js'

const form = reactive({
  username: '',
  password: ''
})

const submitting = ref(false)

onShow(() => {
  if (isLoggedIn()) {
    uni.reLaunch({ url: '/pages/home/index' })
  }
})

/**
 * 校验登录表单，返回错误提示或空字符串
 */
function validateForm() {
  const username = form.username.trim()
  const password = form.password.trim()

  if (!username && !password) {
    return '请输入账号/密码'
  }
  if (!username) {
    return '请输入账号'
  }
  if (!password) {
    return '请输入密码'
  }
  return ''
}

async function handleLogin() {
  const errorMessage = validateForm()
  if (errorMessage) {
    uni.showToast({ title: errorMessage, icon: 'none' })
    return
  }

  submitting.value = true
  try {
    const result = await login({
      username: form.username.trim(),
      password: form.password.trim()
    })

    setToken(result.token)
    const account = result.account || {
      id: result.id,
      username: result.username,
      status: result.status,
      createTime: result.createTime
    }
    if (account.username) {
      setUserInfo(account)
    }

    uni.showToast({ title: '登录成功', icon: 'success' })
    setTimeout(() => {
      uni.reLaunch({ url: '/pages/home/index' })
    }, 300)
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
  background: linear-gradient(180deg, #ecf5ff 0%, #f5f7fa 40%);
}

.login-header {
  margin-bottom: 64rpx;
}

.login-title {
  display: block;
  font-size: 52rpx;
  font-weight: 600;
  color: #303133;
}

.login-subtitle {
  display: block;
  margin-top: 16rpx;
  font-size: 28rpx;
  color: #909399;
}

.login-card {
  padding: 48rpx 40rpx;
  background: #ffffff;
  border-radius: 24rpx;
  box-shadow: 0 8rpx 32rpx rgba(64, 158, 255, 0.08);
}
</style>

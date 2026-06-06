<template>
  <view class="login-page">
    <view class="login-header">
      <text class="login-title">欢迎登录</text>
      <text class="login-subtitle">请输入账号和密码</text>
    </view>

    <view class="login-card">
      <u-form ref="formRef" :model="form" label-width="0">
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
      </u-form>

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
import { onShow } from '@dcloudio/uni-app'
import { accountLogin } from '@/api/account.js'
import { isLoggedIn, setToken, setUserInfo } from '@/utils/auth.js'

const formRef = ref(null)
const submitting = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const inputStyle = {
  backgroundColor: '#f9fafb'
}

/** 已登录则直接进入首页 */
onShow(() => {
  if (isLoggedIn()) {
    uni.reLaunch({ url: '/pages/home/index' })
  }
})

/**
 * 前端表单校验：空输入提示
 * @returns {boolean} 是否通过校验
 */
function validateForm() {
  if (!form.username.trim()) {
    uni.showToast({ title: '请输入账号', icon: 'none' })
    return false
  }
  if (!form.password.trim()) {
    uni.showToast({ title: '请输入密码', icon: 'none' })
    return false
  }
  return true
}

/**
 * 解析服务端登录错误，映射为用户友好提示
 * @param {object} error 接口错误响应
 */
function showLoginError(error) {
  const message = error?.msg || '账号或密码错误'
  uni.showToast({ title: message, icon: 'none' })
}

/** 提交登录 */
async function handleLogin() {
  if (!validateForm() || submitting.value) {
    return
  }

  submitting.value = true
  try {
    const result = await accountLogin({
      username: form.username.trim(),
      password: form.password
    })

    setToken(result.token)
    setUserInfo({
      username: result.username || form.username.trim()
    })

    uni.showToast({ title: '登录成功', icon: 'success' })
    setTimeout(() => {
      uni.reLaunch({ url: '/pages/home/index' })
    }, 500)
  } catch (error) {
    showLoginError(error)
  } finally {
    submitting.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  padding: 120rpx 48rpx 48rpx;
  background: linear-gradient(180deg, #eff6ff 0%, #f3f4f6 40%);
  box-sizing: border-box;
}

.login-header {
  margin-bottom: 64rpx;
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

.login-card {
  background: #ffffff;
  border-radius: 24rpx;
  padding: 48rpx 40rpx;
  box-shadow: 0 8rpx 32rpx rgba(37, 99, 235, 0.08);
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

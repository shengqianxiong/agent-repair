<template>
  <view class="login-page">
    <view class="login-header">
      <text class="login-title">RealCloudAgent</text>
      <text class="login-subtitle">用户端登录</text>
    </view>

    <view class="login-card">
      <u-form ref="formRef" :model="form" :rules="rules" label-width="0">
        <u-form-item prop="username">
          <u-input
            v-model="form.username"
            placeholder="请输入用户名"
            prefix-icon="account"
            clearable
            border="surround"
          />
        </u-form-item>

        <u-form-item prop="password">
          <u-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="lock"
            clearable
            border="surround"
          />
        </u-form-item>
      </u-form>

      <u-button
        type="primary"
        text="登录"
        :loading="loading"
        loading-text="登录中..."
        custom-style="margin-top: 48rpx;"
        @click="handleLogin"
      />
    </view>
  </view>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { appLogin } from '@/api/auth.js'
import { setToken } from '@/utils/auth.js'

const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    {
      required: true,
      message: '请输入用户名',
      trigger: ['blur', 'change']
    }
  ],
  password: [
    {
      required: true,
      message: '请输入密码',
      trigger: ['blur', 'change']
    }
  ]
}

/**
 * 提交登录表单，调用 /app/auth/login 并保存 token
 */
async function handleLogin() {
  if (loading.value) {
    return
  }

  try {
    await formRef.value.validate()
  } catch {
    return
  }

  loading.value = true
  try {
    const data = await appLogin({
      username: form.username.trim(),
      password: form.password
    })

    if (data && data.token) {
      setToken(data.token)
    }

    uni.showToast({
      title: '登录成功',
      icon: 'success'
    })
  } catch {
    // 错误提示由 request 统一处理
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  padding: 120rpx 48rpx 48rpx;
  box-sizing: border-box;
  background: linear-gradient(180deg, #2979ff 0%, #f5f7fa 45%);
}

.login-header {
  margin-bottom: 64rpx;
  text-align: center;
}

.login-title {
  display: block;
  font-size: 48rpx;
  font-weight: 600;
  color: #ffffff;
}

.login-subtitle {
  display: block;
  margin-top: 16rpx;
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.85);
}

.login-card {
  padding: 48rpx 32rpx;
  border-radius: 24rpx;
  background-color: #ffffff;
  box-shadow: 0 12rpx 40rpx rgba(41, 121, 255, 0.12);
}
</style>

<template>
  <view class="login-page page">
    <view class="login-header">
      <text class="login-title">用户登录</text>
      <text class="login-subtitle">极简登录系统</text>
    </view>

    <view class="login-form">
      <u-form :model="form" ref="formRef">
        <u-form-item label="账号" prop="account" border-bottom>
          <u-input v-model="form.account" placeholder="请输入账号" clearable />
        </u-form-item>
        <u-form-item label="密码" prop="password" border-bottom>
          <u-input v-model="form.password" type="password" placeholder="请输入密码" clearable />
        </u-form-item>
      </u-form>

      <u-button
        type="primary"
        text="登录"
        :loading="loading"
        custom-style="margin-top: 48rpx"
        @click="handleLogin"
      />
    </view>
  </view>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { userLogin } from '@/api/index.js'
import { setToken, setAccount } from '@/utils/auth.js'

const loading = ref(false)
const form = reactive({
  account: '',
  password: ''
})

async function handleLogin() {
  if (!form.account.trim()) {
    uni.showToast({ title: '请输入账号', icon: 'none' })
    return
  }
  if (!form.password) {
    uni.showToast({ title: '请输入密码', icon: 'none' })
    return
  }

  loading.value = true
  try {
    const data = await userLogin({
      account: form.account.trim(),
      password: form.password
    })
    setToken(data.token)
    setAccount(data.account)
    uni.showToast({ title: '登录成功', icon: 'success' })
    setTimeout(() => {
      uni.reLaunch({ url: '/pages/home/index' })
    }, 500)
  } catch (e) {
    // 错误提示已在 request 中处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  padding: 120rpx 48rpx 48rpx;
  background: linear-gradient(180deg, #667eea 0%, #f5f7fa 40%);
  justify-content: flex-start;
}
.login-header {
  margin-bottom: 64rpx;
}
.login-title {
  display: block;
  font-size: 48rpx;
  font-weight: 600;
  color: #fff;
}
.login-subtitle {
  display: block;
  margin-top: 12rpx;
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.85);
}
.login-form {
  background: #fff;
  border-radius: 24rpx;
  padding: 48rpx 32rpx;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.08);
}
</style>

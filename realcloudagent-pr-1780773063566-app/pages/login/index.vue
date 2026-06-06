<template>
  <view class="login-page">
    <view class="login-header">
      <text class="login-title">RealCloudAgent</text>
      <text class="login-subtitle">用户端登录</text>
    </view>

    <view class="login-card">
      <u-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <u-form-item label="用户名" prop="username" border-bottom>
          <u-input
            v-model="form.username"
            placeholder="请输入用户名"
            clearable
            border="none"
          />
        </u-form-item>

        <u-form-item label="密码" prop="password" border-bottom>
          <u-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            clearable
            border="none"
          />
        </u-form-item>
      </u-form>

      <u-button
        class="login-button"
        type="primary"
        text="登 录"
        :loading="submitting"
        @click="handleLogin"
      />
    </view>
  </view>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { login } from '@/api/auth.js'
import { isLoggedIn, setToken, setUserInfo } from '@/utils/auth.js'

const formRef = ref(null)
const submitting = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: ['blur', 'change'] }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: ['blur', 'change'] },
    { min: 4, message: '密码至少 4 位', trigger: ['blur', 'change'] }
  ]
}

onLoad(() => {
  if (isLoggedIn()) {
    uni.reLaunch({ url: '/pages/home/index' })
  }
})

/**
 * 提交登录表单，成功后保存 token 并跳转首页
 */
async function handleLogin() {
  if (submitting.value) {
    return
  }

  try {
    await formRef.value.validate()
  } catch {
    return
  }

  submitting.value = true
  try {
    const result = await login({
      username: form.username.trim(),
      password: form.password
    })

    setToken(result.token)
    setUserInfo({
      userId: result.userId,
      username: result.username,
      nickname: result.nickname || result.username
    })

    uni.showToast({ title: '登录成功', icon: 'success' })
    setTimeout(() => {
      uni.reLaunch({ url: '/pages/home/index' })
    }, 400)
  } catch {
    // 错误提示由 request 统一处理
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
  box-sizing: border-box;
}

.login-header {
  margin-bottom: 64rpx;
}

.login-title {
  display: block;
  font-size: 52rpx;
  font-weight: 600;
  color: #1a1a2e;
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
  box-shadow: 0 8rpx 32rpx rgba(41, 121, 255, 0.08);
}

.login-button {
  margin-top: 48rpx;
}
</style>

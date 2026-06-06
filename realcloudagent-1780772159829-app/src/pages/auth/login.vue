<template>
  <view class="login-page">
    <view class="login-card">
      <view class="title">用户端登录</view>
      <u-form ref="formRef" :model="form" :rules="rules" label-width="80">
        <u-form-item label="用户名" prop="username" border-bottom>
          <u-input v-model="form.username" placeholder="请输入用户名" border="none" />
        </u-form-item>
        <u-form-item label="密码" prop="password" border-bottom>
          <u-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            border="none"
          />
        </u-form-item>
      </u-form>
      <u-button
        type="primary"
        text="登录"
        :loading="loading"
        custom-style="margin-top: 40rpx"
        @click="handleLogin"
      />
    </view>
  </view>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { login } from '@/api/auth.js'
import { setToken, setUsername } from '@/utils/auth.js'

const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: ['blur', 'change'] }],
  password: [{ required: true, message: '请输入密码', trigger: ['blur', 'change'] }]
}

async function handleLogin() {
  await formRef.value.validate()
  loading.value = true
  try {
    const data = await login({
      username: form.username,
      password: form.password
    })
    setToken(data.token)
    setUsername(data.username)
    uni.showToast({ title: '登录成功', icon: 'success' })
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  padding: 40rpx;
}
.login-card {
  width: 100%;
  background: #fff;
  border-radius: 16rpx;
  padding: 48rpx 32rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.06);
}
.title {
  font-size: 40rpx;
  font-weight: bold;
  text-align: center;
  margin-bottom: 48rpx;
}
</style>

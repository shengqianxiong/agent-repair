<template>
  <div class="login-page">
    <div class="login-bg">
      <div class="brand">
        <span class="brand-icon">豆</span>
        <h1>豆评助手</h1>
        <p>商家管理端 · 评价返利一站式管理</p>
      </div>
    </div>
    <div class="login-panel">
      <div class="login-card">
        <h2 class="login-title">商家登录</h2>
        <p class="login-subtitle">管理活动、审核评价、追踪返利数据</p>
        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @keyup.enter="handleLogin">
          <el-form-item label="账号" prop="account">
            <el-input v-model="form.account" placeholder="请输入商家账号" clearable size="large" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password size="large" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="loading" size="large" class="login-btn" @click="handleLogin">
              登录
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/modules/auth'
import { setToken, setAccount, setAccountId } from '@/utils/auth'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  account: '',
  password: ''
})

const rules = {
  account: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  await formRef.value.validate()
  loading.value = true
  try {
    const data = await login(form)
    setToken(data.token)
    setAccount(data.account || form.account)
    setAccountId(data.id)
    ElMessage.success('登录成功')
    router.push('/seller/dashboard')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
}

.login-bg {
  flex: 1;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px;
}

.brand {
  color: #fff;
  text-align: center;
}

.brand-icon {
  display: inline-flex;
  width: 64px;
  height: 64px;
  align-items: center;
  justify-content: center;
  border-radius: 16px;
  background: linear-gradient(135deg, var(--dp-primary), var(--dp-primary-light));
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 24px;
}

.brand h1 {
  margin: 0 0 12px;
  font-size: 36px;
  font-weight: 700;
}

.brand p {
  margin: 0;
  font-size: 16px;
  color: rgba(255, 255, 255, 0.7);
}

.login-panel {
  width: 480px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--dp-bg-page);
  padding: 48px 40px;
}

.login-card {
  width: 100%;
  max-width: 360px;
}

.login-title {
  margin: 0 0 8px;
  font-size: 24px;
  font-weight: 600;
  color: var(--dp-text-primary);
}

.login-subtitle {
  margin: 0 0 32px;
  font-size: var(--dp-font-body);
  color: var(--dp-text-secondary);
}

.login-btn {
  width: 100%;
  margin-top: 8px;
}
</style>
